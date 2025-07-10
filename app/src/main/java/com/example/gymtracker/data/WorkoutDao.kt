package com.example.gymtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    // Flow is a stream of data from coroutines. The UI will automatically update when data changes.
    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Insert
    suspend fun insertSession(session: WorkoutSession)

    @Delete
    suspend fun deleteSession(session: WorkoutSession)

    @Query("DELETE FROM workout_sessions")
    suspend fun deleteAllSessions()

    @Query("SELECT * FROM workout_sessions WHERE exerciseName = :exerciseName ORDER BY date ASC")
    fun getSessionsForExercise(exerciseName: String): Flow<List<WorkoutSession>>
}