package com.example.gymtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomFoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customFood: CustomFood)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<CustomFood>)

    @Delete
    suspend fun delete(customFood: CustomFood)

    @Query("SELECT * FROM custom_food ORDER BY name ASC")
    fun getAll(): Flow<List<CustomFood>>

    // --- ADD THIS METHOD ---
    @Query("SELECT COUNT(*) FROM custom_food")
    suspend fun count(): Int
}