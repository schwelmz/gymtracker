package com.example.gymtracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "plan_exercises",
    primaryKeys = ["planId", "exerciseName"],
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlan::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["name"],
            childColumns = ["exerciseName"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ]
)
data class WorkoutPlanExerciseCrossRef(
    val planId: Int,
    val exerciseName: String
)