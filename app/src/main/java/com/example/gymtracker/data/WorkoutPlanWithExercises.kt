package com.example.gymtracker.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class WorkoutPlanWithExercises(
    @Embedded val plan: WorkoutPlan,
    @Relation(
        parentColumn = "id",
        entityColumn = "name",
        associateBy = Junction(
            value = WorkoutPlanExerciseCrossRef::class,
            parentColumn = "planId",
            entityColumn = "exerciseName"
        )
    )
    val exercises: List<Exercise>
)

