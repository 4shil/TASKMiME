package com.fourshil.musicya.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.SettingsPreferences
import com.fourshil.musicya.data.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    val themeMode = settingsPreferences.themeMode
        .stateIn(viewModelScope, SharingStarted.Lazily, ThemeMode.SYSTEM)

    val crossfadeDuration = settingsPreferences.crossfadeDuration
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsPreferences.setThemeMode(mode)
        }
    }

    fun setCrossfadeDuration(seconds: Int) {
        viewModelScope.launch {
            settingsPreferences.setCrossfadeDuration(seconds)
        }
    }
}
