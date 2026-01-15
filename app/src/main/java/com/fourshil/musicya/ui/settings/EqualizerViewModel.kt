package com.fourshil.musicya.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.player.AudioEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EqualizerViewModel @Inject constructor(
    private val audioEngine: AudioEngine
) : ViewModel() {

    val isEnabled = audioEngine.isEnabled
    
    // Map AppBand (from AudioEngine) to local BandState (for UI)
    val bands = audioEngine.bands.map { appBands ->
        appBands.map { 
            BandState(it.index, it.centerFreq, it.level, it.minLevel, it.maxLevel)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bassLevel = audioEngine.bassLevel
    val virtualizerLevel = audioEngine.virtualizerLevel
    val presets = audioEngine.presets
    val currentPreset = audioEngine.currentPreset
    val isInitialized = audioEngine.isInitialized

    fun toggleEnabled(enabled: Boolean) {
        audioEngine.setEnabled(enabled)
    }

    fun setBandLevel(bandIndex: Int, level: Int) {
        audioEngine.setBandLevel(bandIndex, level)
    }

    fun setPreset(presetIndex: Int) {
        audioEngine.setPreset(presetIndex)
    }

    fun setBassLevel(level: Int) {
        audioEngine.setBassLevel(level)
    }

    fun setVirtualizerLevel(level: Int) {
        audioEngine.setVirtualizerLevel(level)
    }
}

data class BandState(
    val index: Int,
    val centerFreq: Int, // in Hz
    val level: Int,
    val minLevel: Int,
    val maxLevel: Int
)
