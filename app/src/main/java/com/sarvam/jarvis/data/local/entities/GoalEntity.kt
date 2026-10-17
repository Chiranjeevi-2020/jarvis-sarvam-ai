package com.sarvam.jarvis.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "LONG_TERM" or "SHORT_TERM"
    val title: String,
    val description: String = "",
    val targetDate: Long, // Epoch timestamp in ms
    val isAchieved: Boolean = false,
    val progressPercent: Int = 0
)
