package com.fourshil.musicya.audiofx

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class EqualizerBand(
    val index: Short,
    val centerFreq: Int, // in milliHertz
    val level: Short // in millibels
)

@Singleton
class AudioFxRepository @Inject constructor() {

    private val _bassBoostStrength = MutableStateFlow<Short>(0) // 0 to 1000
    val bassBoostStrength = _bassBoostStrength.asStateFlow()

    private val _virtualizerStrength = MutableStateFlow<Short>(0) // 0 to 1000
    val virtualizerStrength = _virtualizerStrength.asStateFlow()

    private val _equalizerBands = MutableStateFlow<List<EqualizerBand>>(emptyList())
    val equalizerBands = _equalizerBands.asStateFlow()
    
    private val _equalizerEnabled = MutableStateFlow(true)
    val equalizerEnabled = _equalizerEnabled.asStateFlow()

    fun setBassBoostStrength(strength: Short) {
        _bassBoostStrength.value = strength
    }

    fun setVirtualizerStrength(strength: Short) {
        _virtualizerStrength.value = strength
    }
    
    fun setEqualizerEnabled(enabled: Boolean) {
        _equalizerEnabled.value = enabled
    }

    fun setBandLevel(index: Short, level: Short) {
        val current = _equalizerBands.value.toMutableList()
        val bandIndex = current.indexOfFirst { it.index == index }
        if (bandIndex != -1) {
            current[bandIndex] = current[bandIndex].copy(level = level)
            _equalizerBands.value = current
        }
    }
    
    fun updateBands(bands: List<EqualizerBand>) {
        _equalizerBands.value = bands
    }
}
