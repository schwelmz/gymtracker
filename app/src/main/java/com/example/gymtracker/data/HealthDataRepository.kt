package com.example.gymtracker.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

// A simple data class to hold the stats we fetch
data class TodayHealthStats(
    val steps: Long,
    val distanceMeters: Double,
    val caloriesBurned: Double
)

class HealthDataRepository(private val context: Context) {
    private val healthConnectClient: HealthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    // Define the set of permissions we want to read
    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
    )

    suspend fun hasAllPermissions(): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions().containsAll(permissions)
    }

    suspend fun readTodayHealthStats(): TodayHealthStats? {
        if (!hasAllPermissions()) {
            Log.w("HealthRepo", "Attempted to read data without sufficient permissions.")
            return null
        }

        // Define the time range for today (from midnight to now)
        val end = Instant.now()
        val start = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).toInstant()

        try {
            // 1. Aggregate Steps
            val stepsResponse = healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            val steps = stepsResponse[StepsRecord.COUNT_TOTAL] ?: 0L

            // 2. Aggregate Distance
            val distanceResponse = healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            val distance = distanceResponse[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0

            // 3. Aggregate Calories
            val caloriesResponse = healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            val calories = caloriesResponse[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories ?: 0.0

            return TodayHealthStats(steps, distance, calories)

        } catch (e: Exception) {
            Log.e("HealthRepo", "Error reading health stats", e)
            return null
        }
    }
}