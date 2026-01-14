package com.fourshil.musicya.ui.dsp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.audiofx.AudioFxRepository
import com.fourshil.musicya.audiofx.EqualizerBand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DspViewModel @Inject constructor(
    private val repository: AudioFxRepository
) : ViewModel() {

    val bassBoost = repository.bassBoostStrength
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val virtualizer = repository.virtualizerStrength
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val equalizerBands = repository.equalizerBands
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val equalizerEnabled = repository.equalizerEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    fun setBassBoost(strength: Short) {
        repository.setBassBoostStrength(strength)
    }

    fun setVirtualizer(strength: Short) {
        repository.setVirtualizerStrength(strength)
    }

    fun setBandLevel(index: Short, level: Short) {
        repository.setBandLevel(index, level)
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        repository.setEqualizerEnabled(enabled)
    }
}
