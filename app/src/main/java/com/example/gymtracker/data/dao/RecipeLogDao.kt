package com.example.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gymtracker.data.model.RecipeLog
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeLogDao {
    @Insert
    suspend fun insert(recipeLog: RecipeLog)

    // Update queries to fetch all necessary fields for the diary.
    @Query("SELECT * FROM recipe_logs ORDER BY timestamp DESC")
    fun getAllRecipeLogs(): Flow<List<RecipeLog>>
    @Query("DELETE FROM recipe_logs WHERE id = :logId")
    suspend fun delete(logId: Int)
    @Query("""
        SELECT * FROM recipe_logs
        WHERE timestamp >= :startOfDay AND timestamp < :endOfDay
        ORDER BY timestamp DESC
    """)
    fun getRecipeLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<RecipeLog>>
}