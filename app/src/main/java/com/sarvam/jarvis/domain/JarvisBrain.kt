package com.sarvam.jarvis.domain

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sarvam.jarvis.data.local.JarvisDatabase
import com.sarvam.jarvis.data.local.entities.*
import com.sarvam.jarvis.data.remote.SarvamApiClient
import com.sarvam.jarvis.data.remote.models.ChatCompletionRequest
import com.sarvam.jarvis.data.remote.models.ChatMessageItem
import com.sarvam.jarvis.data.remote.models.TtsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

data class JarvisResult(
    val userText: String,
    val replyText: String,
    val audioBase64: String? = null,
    val actionExecuted: String? = null
)

class JarvisBrain(
    private val context: Context,
    private val database: JarvisDatabase
) {
    private val gson = Gson()

    /**
     * Process voice recording: STT -> Intent Execution -> Database Update -> TTS Voice Response
     */
    suspend fun processVoiceInput(audioFile: File, apiKey: String, preferredLanguage: String = "en-IN"): JarvisResult {
        return withContext(Dispatchers.IO) {
            // 1. Transcribe voice using Sarvam Saaras v4 (supports Tamil, English, Tanglish code-mix)
            val transcript = transcribeAudio(audioFile, apiKey)
                ?: return@withContext JarvisResult(
                    userText = "(Voice recording)",
                    replyText = "Could not transcribe audio. Please check your network or API key."
                )

            // 2. Process text with Brain
            processTextInput(transcript, apiKey, preferredLanguage)
        }
    }

    /**
     * Process text request: Sarvam-105B Reasoning -> Tool Dispatch -> SQLite update -> TTS
     */
    suspend fun processTextInput(inputText: String, apiKey: String, preferredLanguage: String = "en-IN"): JarvisResult {
        return withContext(Dispatchers.IO) {
            // Log user message
            database.chatDao().insertMessage(
                ChatMessageEntity(role = "user", text = inputText)
            )

            if (apiKey.isBlank()) {
                val fallbackReply = handleOfflineFallback(inputText)
                database.chatDao().insertMessage(
                    ChatMessageEntity(role = "assistant", text = fallbackReply)
                )
                return@withContext JarvisResult(userText = inputText, replyText = fallbackReply)
            }

            try {
                // Fetch context from database for grounding
                val pendingAssignments = database.assignmentDao().getPendingAssignments()
                val assignmentSummary = pendingAssignments.joinToString(", ") { "${it.courseName}: ${it.title}" }

                val systemPrompt = """
                    You are Jarvis, a college student's proactive AI personal assistant.
                    You manage schedules, coursework/assignment deadlines, subject study progress %, DSA practice routines, and short-term/long-term goals.
                    You natively understand English, Tamil, and Tanglish (code-mixed Tamil and English).
                    
                    Current Pending Assignments: [$assignmentSummary]
                    
                    Respond warmly and concisely like Jarvis.
                    If the user wants to take an action, append an ACTION command on a new line:
                    - Add Assignment: ACTION:ADD_ASSIGNMENT {"course": "...", "title": "...", "dueDays": 2, "priority": "HIGH"}
                    - Update Subject Study: ACTION:UPDATE_PROGRESS {"subject": "...", "completedUnits": 3, "notes": "..."}
                    - Schedule DSA Session: ACTION:SCHEDULE_DSA {"topic": "...", "startTime": "17:00", "endTime": "18:30"}
                    - Log Solved DSA Problems: ACTION:LOG_DSA {"topic": "...", "solved": 3}
                    - Set Goal: ACTION:SET_GOAL {"type": "SHORT_TERM" or "LONG_TERM", "title": "..."}
                """.trimIndent()

                val request = ChatCompletionRequest(
                    model = "sarvam-105b",
                    messages = listOf(
                        ChatMessageItem(role = "system", content = systemPrompt),
                        ChatMessageItem(role = "user", content = inputText)
                    )
                )

                val response = SarvamApiClient.apiService.chatCompletions(apiKey, request)
                val fullResponse = response.body()?.choices?.firstOrNull()?.message?.content
                    ?: "I'm having trouble connecting to Sarvam AI. Please verify your connection."

                // Parse action and reply
                val (cleanReply, actionTag) = parseAndExecuteAction(fullResponse)

                // Generate Voice Reply via Sarvam Bulbul TTS
                val audioBase64 = generateSpeech(cleanReply, apiKey, preferredLanguage)

                // Save assistant message to local database
                database.chatDao().insertMessage(
                    ChatMessageEntity(
                        role = "assistant",
                        text = cleanReply,
                        intentAction = actionTag
                    )
                )

                JarvisResult(
                    userText = inputText,
                    replyText = cleanReply,
                    audioBase64 = audioBase64,
                    actionExecuted = actionTag
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Offline fallback
                val offlineReply = handleOfflineFallback(inputText)
                database.chatDao().insertMessage(
                    ChatMessageEntity(role = "assistant", text = offlineReply)
                )
                JarvisResult(userText = inputText, replyText = offlineReply)
            }
        }
    }

    private suspend fun transcribeAudio(file: File, apiKey: String): String? {
        return try {
            val requestFile = file.asRequestBody("audio/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val modelPart = "saaras:v4".toRequestBody("text/plain".toMediaTypeOrNull())
            val modePart = "codemix".toRequestBody("text/plain".toMediaTypeOrNull())

            val res = SarvamApiClient.apiService.speechToText(apiKey, filePart, modelPart, modePart)
            res.body()?.transcript
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun generateSpeech(text: String, apiKey: String, languageCode: String): String? {
        return try {
            // Filter out internal action tags if present
            val speechText = text.substringBefore("ACTION:").trim()
            if (speechText.isBlank()) return null

            val req = TtsRequest(
                text = speechText.take(500),
                language_code = languageCode,
                model = "bulbul:v4-flash",
                speaker = "simran_en_customer"
            )
            val res = SarvamApiClient.apiService.textToSpeech(apiKey, req)
            res.body()?.audios?.firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun parseAndExecuteAction(response: String): Pair<String, String?> {
        val actionMarker = "ACTION:"
        if (!response.contains(actionMarker)) {
            return Pair(response.trim(), null)
        }

        val parts = response.split(actionMarker, limit = 2)
        val replyText = parts[0].trim()
        val actionPayload = parts[1].trim()

        var actionExecuted: String? = null

        try {
            when {
                actionPayload.startsWith("ADD_ASSIGNMENT") -> {
                    val jsonStr = actionPayload.removePrefix("ADD_ASSIGNMENT").trim()
                    val json = gson.fromJson(jsonStr, JsonObject::class.java)
                    val course = json.get("course")?.asString ?: "General"
                    val title = json.get("title")?.asString ?: "Coursework Task"
                    val days = json.get("dueDays")?.asLong ?: 2L
                    val priority = json.get("priority")?.asString ?: "MEDIUM"
                    val dueTimestamp = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000)

                    database.assignmentDao().insertAssignment(
                        AssignmentEntity(courseName = course, title = title, dueDate = dueTimestamp, priority = priority)
                    )
                    actionExecuted = "Added Assignment: $course - $title"
                }

                actionPayload.startsWith("UPDATE_PROGRESS") -> {
                    val jsonStr = actionPayload.removePrefix("UPDATE_PROGRESS").trim()
                    val json = gson.fromJson(jsonStr, JsonObject::class.java)
                    val subjectName = json.get("subject")?.asString ?: ""
                    val units = json.get("completedUnits")?.asInt ?: 1
                    val notes = json.get("notes")?.asString ?: ""

                    val existing = database.subjectDao().findSubjectByName(subjectName)
                    if (existing != null) {
                        val pct = ((units.toFloat() / existing.totalUnits.toFloat()) * 100).toInt().coerceIn(0, 100)
                        database.subjectDao().updateSubject(
                            existing.copy(completedUnits = units, percentCompleted = pct, notes = notes)
                        )
                    } else {
                        val pct = ((units.toFloat() / 5f) * 100).toInt()
                        database.subjectDao().insertSubject(
                            SubjectProgressEntity(subjectName = subjectName, totalUnits = 5, completedUnits = units, percentCompleted = pct, notes = notes)
                        )
                    }
                    actionExecuted = "Updated Progress: $subjectName"
                }

                actionPayload.startsWith("SCHEDULE_DSA") -> {
                    val jsonStr = actionPayload.removePrefix("SCHEDULE_DSA").trim()
                    val json = gson.fromJson(jsonStr, JsonObject::class.java)
                    val topic = json.get("topic")?.asString ?: "DSA Practice"
                    val start = json.get("startTime")?.asString ?: "17:00"
                    val end = json.get("endTime")?.asString ?: "18:30"

                    database.scheduleDao().insertSlot(
                        ScheduleEntity(title = "DSA Practice: $topic", category = "DSA", startTime = start, endTime = end)
                    )
                    actionExecuted = "Scheduled DSA Session: $topic at $start"
                }

                actionPayload.startsWith("LOG_DSA") -> {
                    val jsonStr = actionPayload.removePrefix("LOG_DSA").trim()
                    val json = gson.fromJson(jsonStr, JsonObject::class.java)
                    val topic = json.get("topic")?.asString ?: ""
                    val solved = json.get("solved")?.asInt ?: 1

                    val existing = database.dsaDao().findTopic(topic)
                    if (existing != null) {
                        database.dsaDao().updateTopic(
                            existing.copy(
                                problemsSolved = existing.problemsSolved + solved,
                                streakDays = existing.streakDays + 1,
                                lastPracticed = System.currentTimeMillis()
                            )
                        )
                    }
                    actionExecuted = "Logged $solved problems for $topic"
                }

                actionPayload.startsWith("SET_GOAL") -> {
                    val jsonStr = actionPayload.removePrefix("SET_GOAL").trim()
                    val json = gson.fromJson(jsonStr, JsonObject::class.java)
                    val type = json.get("type")?.asString ?: "SHORT_TERM"
                    val title = json.get("title")?.asString ?: "Goal"
                    val days = if (type == "LONG_TERM") 365L else 7L

                    database.goalDao().insertGoal(
                        GoalEntity(type = type, title = title, targetDate = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000))
                    )
                    actionExecuted = "Saved $type Goal: $title"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Pair(replyText.ifBlank { "Task recorded successfully." }, actionExecuted)
    }

    private suspend fun handleOfflineFallback(input: String): String {
        val lower = input.lowercase()
        return when {
            "schedule" in lower && "dsa" in lower -> {
                database.scheduleDao().insertSlot(
                    ScheduleEntity(title = "DSA Practice (Offline logged)", category = "DSA", startTime = "18:00", endTime = "19:30")
                )
                "Scheduled your DSA practice session for 6:00 PM in offline mode."
            }
            "assignment" in lower || "deadline" in lower -> {
                val due = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000)
                database.assignmentDao().insertAssignment(
                    AssignmentEntity(courseName = "Coursework", title = input.take(30), dueDate = due, priority = "HIGH")
                )
                "Noted your assignment deadline for 2 days from now in your local database."
            }
            "solved" in lower || "problem" in lower -> {
                val topic = database.dsaDao().findTopic("Arrays")
                if (topic != null) {
                    database.dsaDao().updateTopic(topic.copy(problemsSolved = topic.problemsSolved + 1, streakDays = topic.streakDays + 1))
                }
                "Great job! Recorded your solved problem and updated your streak in offline storage."
            }
            else -> {
                "Offline Mode: Stored your note locally. When connected to the internet, Sarvam AI will provide full voice and AI planning."
            }
        }
    }
}
