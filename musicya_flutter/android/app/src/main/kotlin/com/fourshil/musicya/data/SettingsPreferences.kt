package com.fourshil.musicya.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

@Singleton
class SettingsPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val themeKey = stringPreferencesKey("theme_mode")
    private val crossfadeKey = intPreferencesKey("crossfade_duration")
    
    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        when (preferences[themeKey]) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }
    
    /**
     * Crossfade duration in seconds. 0 = disabled.
     */
    val crossfadeDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[crossfadeKey] ?: 0
    }
    
    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = when (mode) {
                ThemeMode.SYSTEM -> "system"
                ThemeMode.LIGHT -> "light"
                ThemeMode.DARK -> "dark"
            }
        }
    }
    
    /**
     * Set crossfade duration in seconds.
     * @param seconds Duration (0 = off, valid: 2, 5, 8, 10, 12)
     */
    suspend fun setCrossfadeDuration(seconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[crossfadeKey] = seconds.coerceIn(0, 12)
        }
    }
}

