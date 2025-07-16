package com.example.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gymtracker.data.model.WorkoutPlanCompletion
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WorkoutPlanCompletionDao {
    @Insert
    suspend fun insertCompletion(completion: WorkoutPlanCompletion)

    @Query("SELECT * FROM workout_plan_completions WHERE planId = :planId AND completionDate >= :startDate AND completionDate <= :endDate")
    fun getCompletionsForPlanInDateRange(planId: Int, startDate: LocalDate, endDate: LocalDate): Flow<List<WorkoutPlanCompletion>>

    @Query("SELECT * FROM workout_plan_completions WHERE completionDate >= :startDate AND completionDate <= :endDate")
    fun getCompletionsInDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<WorkoutPlanCompletion>>
}
