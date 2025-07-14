package com.example.gymtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodTemplateDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Ignore if barcode already exists
    suspend fun insert(foodTemplate: FoodTemplate): Long

    @Query("SELECT * FROM food_templates WHERE barcode = :barcode LIMIT 1")
    suspend fun getByBarcode(barcode: String): FoodTemplate?

    @Query("SELECT * FROM food_templates ORDER BY name ASC")
    fun getAll(): Flow<List<FoodTemplate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<FoodTemplate>)

    @Query("SELECT COUNT(*) FROM food_templates")
    suspend fun count(): Int

    @Delete
    suspend fun delete(foodTemplate: FoodTemplate)
}