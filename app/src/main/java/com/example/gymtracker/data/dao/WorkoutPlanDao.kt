package com.example.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gymtracker.data.model.WorkoutPlan
import com.example.gymtracker.data.model.WorkoutPlanExerciseCrossRef
import com.example.gymtracker.data.model.WorkoutPlanWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPlan(plan: WorkoutPlan): Long

    @Update
    suspend fun updatePlan(plan: WorkoutPlan)

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

    @Query("SELECT * FROM workout_plans WHERE id = :planId")
    suspend fun getPlanByIdNow(planId: Int): WorkoutPlan?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertExerciseToPlan(crossRef: WorkoutPlanExerciseCrossRef)

    @Query("DELETE FROM plan_exercises WHERE planId = :planId AND exerciseName = :exerciseName")
    suspend fun removeExerciseFromPlan(planId: Int, exerciseName: String)

    @Query("SELECT * FROM workout_plans WHERE id = :planId")
    fun getPlanWithExercisesNow(planId: Int): WorkoutPlanWithExercises?

}