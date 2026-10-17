package com.sarvam.jarvis.data.local.dao

import androidx.room.*
import com.sarvam.jarvis.data.local.entities.SubjectProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subject_progress ORDER BY subjectName ASC")
    fun getAllSubjects(): Flow<List<SubjectProgressEntity>>

    @Query("SELECT * FROM subject_progress WHERE subjectName LIKE '%' || :name || '%' LIMIT 1")
    suspend fun findSubjectByName(name: String): SubjectProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectProgressEntity): Long

    @Update
    suspend fun updateSubject(subject: SubjectProgressEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectProgressEntity)
}
