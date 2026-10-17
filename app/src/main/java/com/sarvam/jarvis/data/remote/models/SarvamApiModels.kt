package com.sarvam.jarvis.data.remote.models

import com.google.gson.annotations.SerializedName

// STT Response
data class SttResponse(
    @SerializedName("transcript") val transcript: String?
)

// Chat Completion Models
data class ChatCompletionRequest(
    @SerializedName("model") val model: String = "sarvam-105b",
    @SerializedName("messages") val messages: List<ChatMessageItem>
)

data class ChatMessageItem(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class ChatCompletionResponse(
    @SerializedName("choices") val choices: List<ChatChoice>?
)

data class ChatChoice(
    @SerializedName("message") val message: ChatMessageItem?
)

// TTS Models
data class TtsRequest(
    @SerializedName("text") val text: String,
    @SerializedName("language_code") val languageCode: String = "en-IN", // "ta-IN", "hi-IN", "en-IN"
    @SerializedName("model") val model: String = "bulbul:v4-flash",
    @SerializedName("speaker") val speaker: String = "simran_en_customer"
)

data class TtsResponse(
    @SerializedName("audios") val audios: List<String>?
)
