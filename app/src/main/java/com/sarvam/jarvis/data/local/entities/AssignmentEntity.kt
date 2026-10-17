package com.sarvam.jarvis.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseName: String,
    val title: String,
    val dueDate: Long, // Epoch timestamp in ms
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val isSubmitted: Boolean = false
)
