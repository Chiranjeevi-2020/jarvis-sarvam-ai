package com.sarvam.jarvis.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user" or "assistant"
    val text: String,
    val audioFilePath: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val intentAction: String? = null
)
