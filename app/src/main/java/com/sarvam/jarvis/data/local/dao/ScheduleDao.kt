package com.sarvam.jarvis.data.local.dao

import androidx.room.*
import com.sarvam.jarvis.data.local.entities.ScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule ORDER BY startTime ASC")
    fun getAllSchedule(): Flow<List<ScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: ScheduleEntity): Long

    @Update
    suspend fun updateSlot(slot: ScheduleEntity)

    @Delete
    suspend fun deleteSlot(slot: ScheduleEntity)
}
