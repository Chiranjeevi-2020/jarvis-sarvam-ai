package com.sarvam.jarvis.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sarvam.jarvis.data.local.dao.*
import com.sarvam.jarvis.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AssignmentEntity::class,
        SubjectProgressEntity::class,
        DsaTrackerEntity::class,
        GoalEntity::class,
        ScheduleEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class JarvisDatabase : RoomDatabase() {
    abstract fun assignmentDao(): AssignmentDao
    abstract fun subjectDao(): SubjectDao
    abstract fun dsaDao(): DsaDao
    abstract fun goalDao(): GoalDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: JarvisDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): JarvisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JarvisDatabase::class.java,
                    "sarvam_jarvis_db"
                )
                .addCallback(JarvisDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class JarvisDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            suspend fun populateInitialData(database: JarvisDatabase) {
                // Seed standard DSA Topics
                val dsaDao = database.dsaDao()
                val topics = listOf(
                    DsaTrackerEntity(topic = "Arrays & Hashing", problemsSolved = 5, targetProblems = 30, streakDays = 2),
                    DsaTrackerEntity(topic = "Two Pointers & Sliding Window", problemsSolved = 3, targetProblems = 25, streakDays = 1),
                    DsaTrackerEntity(topic = "Trees & Binary Search Trees", problemsSolved = 0, targetProblems = 35),
                    DsaTrackerEntity(topic = "Graphs (BFS/DFS, Dijkstra)", problemsSolved = 0, targetProblems = 30),
                    DsaTrackerEntity(topic = "Dynamic Programming (DP)", problemsSolved = 0, targetProblems = 40)
                )
                topics.forEach { dsaDao.insertTopic(it) }

                // Seed default Subjects
                val subjectDao = database.subjectDao()
                val subjects = listOf(
                    SubjectProgressEntity(subjectName = "Operating Systems", totalUnits = 5, completedUnits = 2, percentCompleted = 40, notes = "Completed Process Scheduling & Memory Management"),
                    SubjectProgressEntity(subjectName = "Computer Networks", totalUnits = 5, completedUnits = 1, percentCompleted = 20, notes = "Studying OSI Model & TCP/IP"),
                    SubjectProgressEntity(subjectName = "Database Management (DBMS)", totalUnits = 5, completedUnits = 3, percentCompleted = 60, notes = "SQL queries and Normalization done")
                )
                subjects.forEach { subjectDao.insertSubject(it) }

                // Seed Sample Assignments
                val assignmentDao = database.assignmentDao()
                val now = System.currentTimeMillis()
                val twoDaysLater = now + (2 * 24 * 60 * 60 * 1000)
                val fiveDaysLater = now + (5 * 24 * 60 * 60 * 1000)
                assignmentDao.insertAssignment(
                    AssignmentEntity(courseName = "Operating Systems", title = "CPU Scheduling Simulator Lab", dueDate = twoDaysLater, priority = "HIGH")
                )
                assignmentDao.insertAssignment(
                    AssignmentEntity(courseName = "Computer Networks", title = "Wireshark Packet Analysis Report", dueDate = fiveDaysLater, priority = "MEDIUM")
                )

                // Seed Default Goals
                val goalDao = database.goalDao()
                val graduationYear = now + (365L * 24 * 60 * 60 * 1000)
                val endOfWeek = now + (7L * 24 * 60 * 60 * 1000)
                goalDao.insertGoal(
                    GoalEntity(type = "LONG_TERM", title = "Land a Software Engineer (SDE) Role", description = "Crack tech interviews, master DSA, and build 2 production full-stack systems", targetDate = graduationYear, progressPercent = 35)
                )
                goalDao.insertGoal(
                    GoalEntity(type = "SHORT_TERM", title = "Complete 10 Tree Problems & OS Unit 2", description = "Target for this week's sprint", targetDate = endOfWeek, progressPercent = 50)
                )

                // Seed Default Schedule
                val scheduleDao = database.scheduleDao()
                scheduleDao.insertSlot(
                    ScheduleEntity(title = "DSA Practice Slot (Trees)", category = "DSA", startTime = "17:00", endTime = "18:30")
                )
                scheduleDao.insertSlot(
                    ScheduleEntity(title = "Coursework Study Block (OS)", category = "STUDY", startTime = "20:00", endTime = "21:30")
                )
            }
        }
    }
}
