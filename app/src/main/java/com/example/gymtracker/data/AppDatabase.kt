package com.example.gymtracker.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gymtracker.data.dao.ExerciseDao
import com.example.gymtracker.data.dao.FoodLogDao
import com.example.gymtracker.data.dao.FoodTemplateDao
import com.example.gymtracker.data.dao.RecipeDao
import com.example.gymtracker.data.dao.RecipeLogDao
import com.example.gymtracker.data.dao.WeightEntryDao
import com.example.gymtracker.data.dao.WorkoutDao
import com.example.gymtracker.data.dao.WorkoutPlanCompletionDao
import com.example.gymtracker.data.dao.WorkoutPlanDao
import com.example.gymtracker.data.model.Converters
import com.example.gymtracker.data.model.Exercise
import com.example.gymtracker.data.model.FoodLog
import com.example.gymtracker.data.model.FoodTemplate
import com.example.gymtracker.data.model.Recipe
import com.example.gymtracker.data.model.RecipeIngredient
import com.example.gymtracker.data.model.WeightEntry
import com.example.gymtracker.data.model.WorkoutPlan
import com.example.gymtracker.data.model.WorkoutPlanCompletion
import com.example.gymtracker.data.model.WorkoutPlanExerciseCrossRef
import com.example.gymtracker.data.model.WorkoutSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.example.gymtracker.data.model.RecipeLog

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
        WorkoutPlanExerciseCrossRef::class,
        RecipeLog::class,
        WorkoutPlanCompletion::class
    ],
    version = 24
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
    abstract fun recipeLogDao(): RecipeLogDao
    abstract fun workoutPlanCompletionDao(): WorkoutPlanCompletionDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_23_24 = object : Migration(23, 24) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS `workout_plan_completions` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `planId` INTEGER NOT NULL,
                `completionDate` INTEGER NOT NULL,
                FOREIGN KEY(`planId`) REFERENCES `workout_plans`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_plan_completions_planId` ON `workout_plan_completions` (`planId`)")
            }
        }

        private val MIGRATION_22_23 = object : Migration(22, 23) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE workout_sessions ADD COLUMN planId INTEGER")
            }
        }

        private val MIGRATION_21_22 = object : Migration(21, 22) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE workout_plans ADD COLUMN goal INTEGER")
            }
        }

        private val MIGRATION_20_21 = object : Migration(20, 21) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `recipe_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `instructions` TEXT NOT NULL, `imageUrl` TEXT, `timestamp` INTEGER NOT NULL)")
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
                    .addMigrations(MIGRATION_20_21, MIGRATION_21_22) // ✅ Use migration instead of destructive fallback
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
