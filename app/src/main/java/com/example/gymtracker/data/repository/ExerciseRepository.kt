package com.example.gymtracker.data.repository

import com.example.gymtracker.R
import com.example.gymtracker.data.model.Exercise

// This object acts as a simple, temporary data source.
object ExerciseRepository {
    fun getAvailableExercises() = listOf(
        Exercise("Bench Press", "Chest, Shoulders, Triceps", imageResId = R.drawable.benchpress),
        Exercise("Squat", "Quads, Glutes, Hamstrings", imageResId = R.drawable.squat),
        Exercise("Deadlift", "Full Body, Back, Legs", imageResId = R.drawable.deadlift),
        Exercise("Overhead Press", "Shoulders, Triceps", imageResId = R.drawable.overheadpress),
        Exercise("Barbell Row", "Back, Biceps", imageResId = R.drawable.barbellrow),
        )
}