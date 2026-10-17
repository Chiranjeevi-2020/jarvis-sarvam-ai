package com.sarvam.jarvis.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dsa_tracker")
data class DsaTrackerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topic: String, // Arrays, Trees, Dynamic Programming, Graphs
    val problemsSolved: Int = 0,
    val targetProblems: Int = 30,
    val difficulty: String = "Medium", // Easy, Medium, Hard
    val streakDays: Int = 0,
    val lastPracticed: Long = System.currentTimeMillis()
)
