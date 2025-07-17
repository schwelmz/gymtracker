package com.example.gymtracker.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a DataStore instance, available application-wide
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    // A key to store the boolean value
    private object PreferencesKeys {
        val HEALTH_PERMISSIONS_DECLINED = booleanPreferencesKey("health_permissions_declined")
        val DISMISSED_DISABLED_CARD = booleanPreferencesKey("dismissed_disabled_card")
    }

    /**
     * A flow that emits true if the user has ever declined health permissions, false otherwise.
     */
    val healthPermissionsDeclinedFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.HEALTH_PERMISSIONS_DECLINED] ?: false
        }

    /**
     * Sets the value for whether the user has declined health permissions.
     */
    suspend fun setHealthPermissionsDeclined(hasDeclined: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HEALTH_PERMISSIONS_DECLINED] = hasDeclined
        }
    }

    val dismissedDisabledCardFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[PreferencesKeys.DISMISSED_DISABLED_CARD] ?: false }

    suspend fun setDismissedDisabledCard(dismissed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.DISMISSED_DISABLED_CARD] = dismissed
        }
    }
}