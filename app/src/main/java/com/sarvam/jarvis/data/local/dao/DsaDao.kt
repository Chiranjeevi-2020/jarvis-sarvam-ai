package com.sarvam.jarvis.data.local.dao

import androidx.room.*
import com.sarvam.jarvis.data.local.entities.DsaTrackerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DsaDao {
    @Query("SELECT * FROM dsa_tracker ORDER BY topic ASC")
    fun getAllDsaTopics(): Flow<List<DsaTrackerEntity>>

    @Query("SELECT * FROM dsa_tracker WHERE topic LIKE '%' || :topicName || '%' LIMIT 1")
    suspend fun findTopic(topicName: String): DsaTrackerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: DsaTrackerEntity): Long

    @Update
    suspend fun updateTopic(topic: DsaTrackerEntity)

    @Query("SELECT SUM(problemsSolved) FROM dsa_tracker")
    fun getTotalProblemsSolved(): Flow<Int?>
}
