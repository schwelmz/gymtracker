package com.example.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.gymtracker.data.model.FoodLog
import com.example.gymtracker.data.model.FoodTemplate
import kotlinx.coroutines.flow.Flow

// A data class to hold the result of the join query
data class FoodLogWithDetails(
    val logId: Int,
    val name: String,
    val imageUrl: String?,
    val grams: Int,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val timestamp: Long
)

@Dao
interface FoodLogDao {
    @Insert
    suspend fun insert(foodLog: FoodLog)

    @Transaction
    @Query("""
        SELECT
        l.id as logId,
        t.name,
        t.imageUrl,
        l.grams,
        l.calories,           -- ✅ use stored values
        l.protein,
        l.carbs,
        l.fat,
        l.timestamp
        FROM food_logs l
        JOIN food_templates t ON l.templateId = t.id
        WHERE l.timestamp >= :startOfDay AND l.timestamp < :endOfDay
        ORDER BY l.timestamp DESC
    """)
    fun getLogsForDayWithDetails(startOfDay: Long, endOfDay: Long): Flow<List<FoodLogWithDetails>>

    @Transaction
    @Query("""
        SELECT
        l.id as logId,
        t.name,
        t.imageUrl,
        l.grams,
        l.calories,           -- ✅ use stored values
        l.protein,
        l.carbs,
        l.fat,
        l.timestamp
        FROM food_logs l
        JOIN food_templates t ON l.templateId = t.id
        ORDER BY l.timestamp DESC
    """)
    fun getAllLogsWithDetails(): Flow<List<FoodLogWithDetails>>
    @Query("UPDATE food_logs SET grams = :newGrams WHERE id = :logId")
    suspend fun updateGrams(logId: Int, newGrams: Int)
    @Query("UPDATE food_logs SET timestamp = :newTimestamp WHERE id = :logId")
    suspend fun updateTimestamp(logId: Int, newTimestamp: Long)
    @Query("DELETE FROM food_logs WHERE id = :logId")
    suspend fun delete(logId: Int)
    @Query("SELECT * FROM food_logs WHERE id = :logId")
    suspend fun getById(logId: Int): FoodLog
    @Query("UPDATE food_logs SET grams = :grams, calories = :calories, protein = :protein, carbs = :carbs, fat = :fat WHERE id = :logId")
    suspend fun updateFoodLogFull(
        logId: Int,
        grams: Int,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int
    )
    @Query("UPDATE food_logs SET grams = :grams, timestamp = :timestamp WHERE id = :logId")
    suspend fun updateFoodLog(
        logId: Int,
        grams: Int,
        timestamp: Long,
    )

}