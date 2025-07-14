package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gymtracker.data.AppDatabase
import com.example.gymtracker.data.WeightEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

class WeightViewModel(application: Application) : AndroidViewModel(application) {
    private val weightDao = AppDatabase.getDatabase(application).weightEntryDao()

    val allWeightEntries: Flow<List<WeightEntry>> = weightDao.getAllEntries()

    // Updated to accept the image URI
    fun addOrUpdateWeight(date: LocalDate, weight: Float, imageUri: String?) {
        viewModelScope.launch {
            val existingEntry = weightDao.getByDate(date)

            if (existingEntry != null) {
                // Update existing entry if any value has changed
                if (existingEntry.weight != weight || existingEntry.imageUri != imageUri) {
                    val updatedEntry = existingEntry.copy(weight = weight, imageUri = imageUri)
                    weightDao.updateWeight(updatedEntry)
                }
            } else {
                // Insert new entry if none exists for the given date
                val newEntry = WeightEntry(date = date, weight = weight, imageUri = imageUri)
                weightDao.insert(newEntry)
            }
        }
    }

    fun deleteWeight(entry: WeightEntry) {
        viewModelScope.launch {
            weightDao.delete(entry)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return WeightViewModel(application) as T
            }
        }
    }
}