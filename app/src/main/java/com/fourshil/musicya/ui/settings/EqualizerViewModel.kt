package com.fourshil.musicya.ui.settings

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import androidx.lifecycle.ViewModel
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EqualizerViewModel @Inject constructor(
    private val playerController: PlayerController
) : ViewModel() {

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    private val _isEnabled = MutableStateFlow(false)
    val isEnabled = _isEnabled.asStateFlow()

    private val _bands = MutableStateFlow<List<BandState>>(emptyList())
    val bands = _bands.asStateFlow()

    private val _bassLevel = MutableStateFlow(0)
    val bassLevel = _bassLevel.asStateFlow()

    private val _virtualizerLevel = MutableStateFlow(0)
    val virtualizerLevel = _virtualizerLevel.asStateFlow()

    private val _presets = MutableStateFlow<List<String>>(emptyList())
    val presets = _presets.asStateFlow()

    private val _currentPreset = MutableStateFlow(-1)
    val currentPreset = _currentPreset.asStateFlow()

    fun initialize(audioSessionId: Int) {
        if (audioSessionId == 0) return

        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = false
            }
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = false
            }
            virtualizer = Virtualizer(0, audioSessionId).apply {
                enabled = false
            }

            updateBands()
            updatePresets()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateBands() {
        equalizer?.let { eq ->
            val bandList = mutableListOf<BandState>()
            val minLevel = eq.bandLevelRange[0]
            val maxLevel = eq.bandLevelRange[1]

            for (i in 0 until eq.numberOfBands) {
                val band = i.toShort()
                bandList.add(
                    BandState(
                        index = i,
                        centerFreq = eq.getCenterFreq(band) / 1000, // Hz to kHz
                        level = eq.getBandLevel(band).toInt(),
                        minLevel = minLevel.toInt(),
                        maxLevel = maxLevel.toInt()
                    )
                )
            }
            _bands.value = bandList
        }
    }

    private fun updatePresets() {
        equalizer?.let { eq ->
            val presetList = mutableListOf<String>()
            for (i in 0 until eq.numberOfPresets) {
                presetList.add(eq.getPresetName(i.toShort()))
            }
            _presets.value = presetList
        }
    }

    fun toggleEnabled(enabled: Boolean) {
        _isEnabled.value = enabled
        equalizer?.enabled = enabled
        bassBoost?.enabled = enabled
        virtualizer?.enabled = enabled
    }

    fun setBandLevel(bandIndex: Int, level: Int) {
        equalizer?.setBandLevel(bandIndex.toShort(), level.toShort())
        val updated = _bands.value.toMutableList()
        updated[bandIndex] = updated[bandIndex].copy(level = level)
        _bands.value = updated
        _currentPreset.value = -1 // Custom
    }

    fun setPreset(presetIndex: Int) {
        equalizer?.usePreset(presetIndex.toShort())
        _currentPreset.value = presetIndex
        updateBands()
    }

    fun setBassLevel(level: Int) {
        bassBoost?.setStrength(level.toShort())
        _bassLevel.value = level
    }

    fun setVirtualizerLevel(level: Int) {
        virtualizer?.setStrength(level.toShort())
        _virtualizerLevel.value = level
    }

    override fun onCleared() {
        super.onCleared()
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
    }
}

data class BandState(
    val index: Int,
    val centerFreq: Int, // in Hz
    val level: Int,
    val minLevel: Int,
    val maxLevel: Int
)
