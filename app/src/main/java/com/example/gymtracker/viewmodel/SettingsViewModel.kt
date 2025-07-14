package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private val _language = MutableStateFlow("English")
    val language: StateFlow<String> = _language

    init {
        DataStoreManager.getLanguageFlow(context)
            .onEach { _language.value = it }
            .launchIn(viewModelScope)
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            DataStoreManager.saveLanguage(context, language)
        }
    }
}
