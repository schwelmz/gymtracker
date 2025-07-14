package com.example.gymtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: WorkoutPlan): Long

    @Delete
    suspend fun deletePlan(plan: WorkoutPlan)

    @Query("SELECT * FROM workout_plans")
    fun getAllPlans(): Flow<List<WorkoutPlan>>

    @Transaction
    @Query("SELECT * FROM workout_plans")
    fun getAllPlansWithExercises(): Flow<List<WorkoutPlanWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout_plans WHERE id = :planId")
    fun getPlanWithExercises(planId: Int): Flow<WorkoutPlanWithExercises>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseToPlan(crossRef: WorkoutPlanExerciseCrossRef)

    @Query("DELETE FROM plan_exercises WHERE planId = :planId AND exerciseName = :exerciseName")
    suspend fun removeExerciseFromPlan(planId: Int, exerciseName: String)
    @Query("SELECT * FROM workout_plans WHERE id = :planId")
    fun getPlanWithExercisesNow(planId: Int): WorkoutPlanWithExercises?

}
