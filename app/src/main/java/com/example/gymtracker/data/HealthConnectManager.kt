package com.example.gymtracker.data

import android.content.Context
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController

/**
 * Manages availability and permission launching for Health Connect.
 */
class HealthConnectManager(private val context: Context) {

    val healthConnectAvailability: Int
        get() = HealthConnectClient.getSdkStatus(context)

    /**
     * Creates an ActivityResultContract to request Health Connect permissions.
     */
    fun requestPermissionsContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }
}