package com.sarvam.jarvis.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subject_progress")
data class SubjectProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val totalUnits: Int = 5,
    val completedUnits: Int = 0,
    val percentCompleted: Int = 0,
    val notes: String = ""
)
