package com.example.gymtracker.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "settings"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object DataStoreManager {
    private val LANGUAGE_KEY = stringPreferencesKey("language")

    fun getLanguageFlow(context: Context): Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: "English" // default
        }

    suspend fun saveLanguage(context: Context, language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }
}
