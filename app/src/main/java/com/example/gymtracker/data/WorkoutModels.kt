package com.example.gymtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.time.LocalDate
import java.util.Date

// Represents a single set of an exercise (e.g., 10 reps at 50 kg)
data class ExerciseSet(
    val reps: Int,
    val weight: Double
)

// Represents a full workout session for one type of exercise on a specific day
@Entity(tableName = "workout_sessions") // Tells Room this is a table
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) // Makes the ID the primary key and auto-generates it
    val id: Int = 0, // Unique ID for the database
    val exerciseName: String,
    val sets: List<ExerciseSet>,
    val date: Date
)

// This class tells Room how to convert complex types (Date, List) to simple types it can store.
class Converters {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun fromExerciseSetList(value: List<ExerciseSet>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ExerciseSet>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toExerciseSetList(value: String): List<ExerciseSet> {
        val gson = Gson()
        val type = object : TypeToken<List<ExerciseSet>>() {}.type
        return gson.fromJson(value, type)
    }
}