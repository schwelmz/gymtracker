package com.example.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gymtracker.data.model.FoodTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodTemplateDao {
    @Query("SELECT * FROM food_templates ORDER BY name ASC")
    fun getAll(): Flow<List<FoodTemplate>>

    @Query("SELECT * FROM food_templates WHERE id = :id")
    suspend fun getById(id: Int): FoodTemplate?

    @Query("SELECT * FROM food_templates WHERE barcode = :barcode")
    suspend fun getByBarcode(barcode: String): FoodTemplate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: FoodTemplate): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(templates: List<FoodTemplate>)

    @Update
    suspend fun update(template: FoodTemplate)

    @Delete
    suspend fun delete(template: FoodTemplate)

    @Query("SELECT COUNT(*) FROM food_templates")
    suspend fun count(): Int
}