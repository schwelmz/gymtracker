package com.example.gymtracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WeightEntryDao {
    // Using REPLACE strategy means adding an entry for an existing date simply updates it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weightEntry: WeightEntry)

    @Delete
    suspend fun delete(weightEntry: WeightEntry)

    // Get all entries, sorted by date for the chart and list.
    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<WeightEntry>>
}