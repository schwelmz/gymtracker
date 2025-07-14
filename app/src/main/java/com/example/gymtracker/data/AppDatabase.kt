package com.example.gymtracker.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 17
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
        private val MIGRATION_INCREMENT= object : Migration(16, 17) {
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
