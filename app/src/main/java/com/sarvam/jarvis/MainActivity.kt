package com.sarvam.jarvis

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarvam.jarvis.audio.AudioPlayer
import com.sarvam.jarvis.data.local.entities.AssignmentEntity
import com.sarvam.jarvis.data.local.entities.DsaTrackerEntity
import com.sarvam.jarvis.data.local.entities.SubjectProgressEntity
import com.sarvam.jarvis.notifications.ReminderManager
import com.sarvam.jarvis.ui.screens.*
import com.sarvam.jarvis.ui.theme.*
import kotlinx.coroutines.launch

enum class ScreenTab(val title: String) {
    CHAT("Voice/Chat"),
    DASHBOARD("Dashboard"),
    DEADLINES("Deadlines"),
    DSA("DSA"),
    PROGRESS("Progress"),
    GOALS("Goals")
}

class MainActivity : ComponentActivity() {

    private val prefs: SharedPreferences by lazy {
        getSharedPreferences("sarvam_jarvis_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as JarvisApp
        val database = app.database
        val brain = app.brain
        val reminderManager = ReminderManager(this)
        val audioPlayer = AudioPlayer(this)

        setContent {
            SarvamJarvisTheme {
                val coroutineScope = rememberCoroutineScope()
                var currentTab by remember { mutableStateOf(ScreenTab.CHAT) }
                var showSettings by remember { mutableStateOf(false) }

                // State backed by SharedPreferences
                var apiKey by remember {
                    mutableStateOf(prefs.getString("sarvam_api_key", "") ?: "")
                }
                var preferredLanguage by remember {
                    mutableStateOf(prefs.getString("sarvam_language", "ta-IN") ?: "ta-IN")
                }

                // Observables from Room Database
                val messages by database.chatDao().getAllMessages().collectAsState(initial = emptyList())
                val assignments by database.assignmentDao().getAllAssignments().collectAsState(initial = emptyList())
                val dsaTopics by database.dsaDao().getAllDsaTopics().collectAsState(initial = emptyList())
                val subjects by database.subjectDao().getAllSubjects().collectAsState(initial = emptyList())
                val goals by database.goalDao().getAllGoals().collectAsState(initial = emptyList())
                val schedule by database.scheduleDao().getAllSchedule().collectAsState(initial = emptyList())
                val totalDsaSolved by database.dsaDao().getTotalProblemsSolved().collectAsState(initial = 0)

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                    Text(
                                        text = "JARVIS",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = CyanGlow,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${currentTab.title}",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { showSettings = true }) {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = if (apiKey.isNotBlank()) CyanGlow else AccentAmber
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = DarkBackground,
                                titleContentColor = TextPrimary
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = DarkSurface,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = currentTab == ScreenTab.CHAT,
                                onClick = { currentTab = ScreenTab.CHAT },
                                icon = { Icon(Icons.Default.Mic, contentDescription = "Voice") },
                                label = { Text("Voice", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = CyanGlow,
                                    indicatorColor = CyanGlow,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == ScreenTab.DASHBOARD,
                                onClick = { currentTab = ScreenTab.DASHBOARD },
                                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                                label = { Text("Home", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = CyanGlow,
                                    indicatorColor = CyanGlow,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == ScreenTab.DEADLINES,
                                onClick = { currentTab = ScreenTab.DEADLINES },
                                icon = { Icon(Icons.Default.Assignment, contentDescription = "Deadlines") },
                                label = { Text("Tasks", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = CyanGlow,
                                    indicatorColor = CyanGlow,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == ScreenTab.DSA,
                                onClick = { currentTab = ScreenTab.DSA },
                                icon = { Icon(Icons.Default.Code, contentDescription = "DSA") },
                                label = { Text("DSA", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = CyanGlow,
                                    indicatorColor = CyanGlow,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == ScreenTab.PROGRESS,
                                onClick = { currentTab = ScreenTab.PROGRESS },
                                icon = { Icon(Icons.Default.MenuBook, contentDescription = "Progress") },
                                label = { Text("Study", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = CyanGlow,
                                    indicatorColor = CyanGlow,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == ScreenTab.GOALS,
                                onClick = { currentTab = ScreenTab.GOALS },
                                icon = { Icon(Icons.Default.Flag, contentDescription = "Goals") },
                                label = { Text("Goals", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = CyanGlow,
                                    indicatorColor = CyanGlow,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                )
                            )
                        }
                    },
                    containerColor = DarkBackground
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentTab) {
                            ScreenTab.CHAT -> ChatVoiceScreen(
                                messages = messages,
                                brain = brain,
                                apiKey = apiKey,
                                preferredLanguage = preferredLanguage
                            )
                            ScreenTab.DASHBOARD -> DashboardScreen(
                                assignments = assignments,
                                scheduleSlots = schedule,
                                goals = goals,
                                totalDsaSolved = totalDsaSolved ?: 0,
                                onSpeakBriefing = {
                                    coroutineScope.launch {
                                        val briefingPrompt = "Give me a snappy morning daily briefing for today covering my pending assignments and schedule."
                                        val res = brain.processTextInput(briefingPrompt, apiKey, preferredLanguage)
                                        res.audioBase64?.let { audioPlayer.playBase64Audio(it) }
                                    }
                                }
                            )
                            ScreenTab.DEADLINES -> DeadlinesScreen(
                                assignments = assignments,
                                onToggleSubmitted = { item ->
                                    coroutineScope.launch {
                                        database.assignmentDao().updateAssignment(item.copy(isSubmitted = !item.isSubmitted))
                                    }
                                },
                                onDeleteAssignment = { item ->
                                    coroutineScope.launch {
                                        database.assignmentDao().deleteAssignment(item)
                                    }
                                },
                                onAddAssignment = { course, title, days, priority ->
                                    coroutineScope.launch {
                                        val due = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000)
                                        database.assignmentDao().insertAssignment(
                                            AssignmentEntity(courseName = course, title = title, dueDate = due, priority = priority)
                                        )
                                        // Schedule offline reminder alarm 3 hours before due date
                                        val reminderTime = due - (3 * 60 * 60 * 1000)
                                        reminderManager.scheduleReminder(
                                            reminderTime,
                                            "Upcoming Deadline: $course",
                                            "$title is due in 3 hours!"
                                        )
                                    }
                                }
                            )
                            ScreenTab.DSA -> DsaScreen(
                                topics = dsaTopics,
                                onIncrementSolved = { topic ->
                                    coroutineScope.launch {
                                        database.dsaDao().updateTopic(
                                            topic.copy(
                                                problemsSolved = topic.problemsSolved + 1,
                                                streakDays = topic.streakDays + 1,
                                                lastPracticed = System.currentTimeMillis()
                                            )
                                        )
                                    }
                                }
                            )
                            ScreenTab.PROGRESS -> ProgressScreen(
                                subjects = subjects,
                                onIncrementUnit = { sub ->
                                    coroutineScope.launch {
                                        val newUnits = (sub.completedUnits + 1).coerceAtMost(sub.totalUnits)
                                        val pct = ((newUnits.toFloat() / sub.totalUnits.toFloat()) * 100).toInt()
                                        database.subjectDao().updateSubject(
                                            sub.copy(completedUnits = newUnits, percentCompleted = pct)
                                        )
                                    }
                                }
                            )
                            ScreenTab.GOALS -> GoalsScreen(goals = goals)
                        }
                    }
                }

                if (showSettings) {
                    SettingsDialog(
                        currentApiKey = apiKey,
                        currentLanguage = preferredLanguage,
                        onDismiss = { showSettings = false },
                        onSave = { newKey, newLang ->
                            apiKey = newKey
                            preferredLanguage = newLang
                            prefs.edit()
                                .putString("sarvam_api_key", newKey)
                                .putString("sarvam_language", newLang)
                                .apply()
                        }
                    )
                }
            }
        }
    }
}
