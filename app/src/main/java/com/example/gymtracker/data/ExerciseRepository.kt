package com.example.gymtracker.data


// This object acts as a simple, temporary data source.
object ExerciseRepository {
    fun getAvailableExercises() = listOf(
        Exercise("Bench Press", "Chest, Shoulders, Triceps"),
        Exercise("Squat", "Quads, Glutes, Hamstrings"),
        Exercise("Deadlift", "Full Body, Back, Legs"),
        Exercise("Overhead Press", "Shoulders, Triceps"),
        Exercise("Barbell Row", "Back, Biceps")
    )
}