package com.sarvam.jarvis.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "DSA", // DSA, LECTURE, ASSIGNMENT, PROJECT, REVISION
    val startTime: String, // e.g. "17:00"
    val endTime: String,   // e.g. "19:00"
    val dayOfWeek: String = "DAILY", // "Monday", "Tuesday", etc., or "DAILY"
    val isCompleted: Boolean = false
)
