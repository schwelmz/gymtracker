package com.example.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.gymtracker.data.model.RecipeLog
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeLogDao {
    @Insert
    suspend fun insert(recipeLog: RecipeLog)

    @Query("SELECT id, name, imageUrl, instructions, timestamp FROM recipe_logs")
    fun getAllRecipeLogs(): Flow<List<RecipeLog>>

    @Transaction
    @Query("""
        SELECT
        l.id,
        l.name,
        l.imageUrl,
        l.instructions,
        l.timestamp
        FROM recipe_logs l
        WHERE l.timestamp >= :startOfDay AND l.timestamp < :endOfDay
        ORDER BY l.timestamp DESC
    """)
    fun getRecipeLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<RecipeLog>>

}