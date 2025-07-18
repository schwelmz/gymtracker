package com.example.gymtracker.data.dao

import androidx.room.*
import com.example.gymtracker.data.model.WeightEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WeightEntryDao {
    // Using REPLACE strategy means adding an entry for an existing date simply updates it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weightEntry: WeightEntry)
    @Update
    suspend fun updateWeight(entry: WeightEntry)
    @Delete
    suspend fun delete(weightEntry: WeightEntry)
    @Query("SELECT * FROM weight_entries WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: LocalDate): WeightEntry?
    // Get all entries, sorted by date for the chart and list.
    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<WeightEntry>>
}