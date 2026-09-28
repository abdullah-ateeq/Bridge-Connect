package com.example.bridgeconnect.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bridgeconnect.data.AppThemeMode
import com.example.bridgeconnect.data.BoardThemeStyle
import com.example.bridgeconnect.data.GamePreferencesRepository
import com.example.bridgeconnect.data.GameStatistics
import com.example.bridgeconnect.data.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GamePreferencesRepository(application.applicationContext)

    val settings: StateFlow<UserSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UserSettings()
    )

    val statistics: StateFlow<GameStatistics> = repository.statisticsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GameStatistics()
    )

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
        }
    }

    fun setBoardTheme(theme: BoardThemeStyle) {
        viewModelScope.launch {
            repository.setBoardTheme(theme)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setSoundEnabled(enabled)
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setHapticsEnabled(enabled)
        }
    }

    fun setShowLastMove(enabled: Boolean) {
        viewModelScope.launch {
            repository.setShowLastMove(enabled)
        }
    }

    fun setHighlightTargetSides(enabled: Boolean) {
        viewModelScope.launch {
            repository.setHighlightTargetSides(enabled)
        }
    }

    fun resetStatistics() {
        viewModelScope.launch {
            repository.resetStatistics()
        }
    }
}
