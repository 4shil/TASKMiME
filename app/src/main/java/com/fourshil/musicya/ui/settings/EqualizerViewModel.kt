package com.fourshil.musicya.ui.settings

import androidx.lifecycle.ViewModel
import com.fourshil.musicya.player.AudioEngine
import com.fourshil.musicya.player.EqBand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * ViewModel for the Equalizer screen.
 * Exposes AudioEngine state and provides control methods.
 */
@HiltViewModel
class EqualizerViewModel @Inject constructor(
    private val audioEngine: AudioEngine
) : ViewModel() {

    // ========== State from AudioEngine ==========
    
    val isInitialized: StateFlow<Boolean> = audioEngine.isInitialized
    val isEnabled: StateFlow<Boolean> = audioEngine.isEnabled
    val bands: StateFlow<List<EqBand>> = audioEngine.bands
    val presets: StateFlow<List<String>> = audioEngine.presets
    val currentPreset: StateFlow<Int> = audioEngine.currentPresetIndex
    val bassLevel: StateFlow<Int> = audioEngine.bassStrength
    val virtualizerLevel: StateFlow<Int> = audioEngine.virtualizerStrength
    val loudnessLevel: StateFlow<Int> = audioEngine.loudnessGain

    // ========== EQ Controls ==========
    
    fun toggleEnabled(enabled: Boolean) {
        audioEngine.setEnabled(enabled)
    }

    fun setBandLevel(bandIndex: Int, level: Int) {
        audioEngine.setBandLevel(bandIndex, level)
    }

    fun setPreset(presetIndex: Int) {
        audioEngine.setPreset(presetIndex)
    }
    
    fun resetEqualizer() {
        audioEngine.resetEqualizer()
    }

    // ========== FX Controls ==========
    
    fun setBassLevel(level: Int) {
        audioEngine.setBassStrength(level)
    }

    fun setVirtualizerLevel(level: Int) {
        audioEngine.setVirtualizerStrength(level)
    }
    
    fun setLoudnessLevel(level: Int) {
        audioEngine.setLoudnessGain(level)
    }
}
