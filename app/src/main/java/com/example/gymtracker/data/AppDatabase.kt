package com.example.gymtracker.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gymtracker.data.dao.ExerciseDao
import com.example.gymtracker.data.dao.FoodLogDao
import com.example.gymtracker.data.dao.FoodTemplateDao
import com.example.gymtracker.data.dao.RecipeDao
import com.example.gymtracker.data.dao.WeightEntryDao
import com.example.gymtracker.data.dao.WorkoutDao
import com.example.gymtracker.data.dao.WorkoutPlanDao
import com.example.gymtracker.data.model.Converters
import com.example.gymtracker.data.model.Exercise
import com.example.gymtracker.data.model.FoodLog
import com.example.gymtracker.data.model.FoodTemplate
import com.example.gymtracker.data.model.Recipe
import com.example.gymtracker.data.model.RecipeIngredient
import com.example.gymtracker.data.model.WeightEntry
import com.example.gymtracker.data.model.WorkoutPlan
import com.example.gymtracker.data.model.WorkoutPlanExerciseCrossRef
import com.example.gymtracker.data.model.WorkoutSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [
        WorkoutSession::class,
        Exercise::class,
        FoodTemplate::class,
        FoodLog::class,
        WeightEntry::class,
        Recipe::class,
        RecipeIngredient::class,
        WorkoutPlan::class,
        WorkoutPlanExerciseCrossRef::class
    ],
    version = 20
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun weightEntryDao(): WeightEntryDao
    abstract fun foodTemplateDao(): FoodTemplateDao
    abstract fun recipeDao(): RecipeDao
    abstract fun foodLogDao(): FoodLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 14 to 15
        private val MIGRATION_INCREMENT= object : Migration(20, 20) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add any table or column creation here — adjust if needed!
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val scope = CoroutineScope(Dispatchers.IO)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gym_tracker_database"
                )
                    .addMigrations(MIGRATION_INCREMENT) // ✅ Use migration instead of destructive fallback
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
