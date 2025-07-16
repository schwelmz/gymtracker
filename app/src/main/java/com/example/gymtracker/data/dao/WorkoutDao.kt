package com.example.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gymtracker.data.model.WorkoutSession
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface WorkoutDao {
    // Flow is a stream of data from coroutines. The UI will automatically update when data changes.
    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    fun getSessionById(sessionId: Int): Flow<WorkoutSession>

    @Insert
    suspend fun insertSession(session: WorkoutSession)

    @Update
    suspend fun updateSession(session: WorkoutSession)

    @Delete
    suspend fun deleteSession(session: WorkoutSession)

    @Query("DELETE FROM workout_sessions")
    suspend fun deleteAllSessions()

    @Query("SELECT * FROM workout_sessions WHERE exerciseName = :exerciseName ORDER BY date ASC")
    fun getSessionsForExercise(exerciseName: String): Flow<List<WorkoutSession>>

    @Query("DELETE FROM workout_sessions WHERE exerciseName = :exerciseName")
    suspend fun deleteSessionsByExerciseName(exerciseName: String)

    @Query("SELECT DISTINCT date FROM workout_sessions")
    fun getAllWorkoutDates(): Flow<List<Date>>

    @Query("SELECT * FROM workout_sessions WHERE date BETWEEN :startDate AND :endDate")
    fun getSessionsInDateRange(startDate: Date, endDate: Date): Flow<List<WorkoutSession>>
}