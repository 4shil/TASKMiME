package com.fourshil.musicya.player

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.Virtualizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioEngine @Inject constructor() {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null
    
    private var currentSessionId: Int = 0

    // State Flows for UI observation
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized = _isInitialized.asStateFlow()

    private val _bands = MutableStateFlow<List<AppBand>>(emptyList())
    val bands = _bands.asStateFlow()

    private val _presets = MutableStateFlow<List<String>>(emptyList())
    val presets = _presets.asStateFlow()

    private val _currentPreset = MutableStateFlow(-1) // -1 is custom
    val currentPreset = _currentPreset.asStateFlow()

    private val _bassLevel = MutableStateFlow(0)
    val bassLevel = _bassLevel.asStateFlow()

    private val _virtualizerLevel = MutableStateFlow(0)
    val virtualizerLevel = _virtualizerLevel.asStateFlow()
    
    private val _loudnessLevel = MutableStateFlow(0)
    val loudnessLevel = _loudnessLevel.asStateFlow()

    private val _isEnabled = MutableStateFlow(false)
    val isEnabled = _isEnabled.asStateFlow()

    suspend fun attach(sessionId: Int) = withContext(Dispatchers.Default) {
        if (sessionId == 0 || sessionId == currentSessionId) return@withContext
        
        Log.d("AudioEngine", "Attaching to session: $sessionId")
        release() // Release previous session effects
        
        currentSessionId = sessionId
        
        // 1. Equalizer
        try {
            equalizer = Equalizer(0, sessionId).apply {
                enabled = _isEnabled.value
            }
        } catch (e: Exception) {
            Log.e("AudioEngine", "Equalizer init failed", e)
            equalizer = null
        }
        setupEqualizerBands()
        setupPresets()

        // 2. Bass Boost
        try {
            bassBoost = BassBoost(0, sessionId).apply {
                enabled = _isEnabled.value
                try {
                    setStrength(_bassLevel.value.toShort())
                } catch (e: Exception) { Log.e("AudioEngine", "Bass init failed", e) }
            }
        } catch (e: Exception) {
            Log.e("AudioEngine", "BassBoost init failed", e)
            bassBoost = null
        }

        // 3. Virtualizer
        try {
            virtualizer = Virtualizer(0, sessionId).apply {
                enabled = _isEnabled.value
                try {
                    setStrength(_virtualizerLevel.value.toShort())
                } catch (e: Exception) { Log.e("AudioEngine", "Virt init failed", e) }
            }
        } catch (e: Exception) {
            Log.e("AudioEngine", "Virtualizer init failed", e)
            virtualizer = null
        }
        
        // 4. Loudness Enhancer (Volume Boost)
        try {
            loudnessEnhancer = LoudnessEnhancer(sessionId).apply {
                enabled = _isEnabled.value
                setTargetGain(_loudnessLevel.value * 10) // mB
            }
        } catch (e: Exception) {
            Log.e("AudioEngine", "LoudnessEnhancer not supported", e)
            loudnessEnhancer = null
        }

        // Always mark as initialized so UI is available
        _isInitialized.value = true
    }

    private fun setupEqualizerBands() {
        val eq = equalizer
        val bandList = mutableListOf<AppBand>()
        
        // Dynamically detect bands from hardware
        if (eq != null && eq.numberOfBands > 0) {
            val minLevel = eq.bandLevelRange[0]
            val maxLevel = eq.bandLevelRange[1]
            val numBands = eq.numberOfBands
            
            for (i in 0 until numBands) {
                val bandMsg = i.toShort()
                bandList.add(
                    AppBand(
                        index = i,
                        centerFreq = eq.getCenterFreq(bandMsg) / 1000,
                        level = eq.getBandLevel(bandMsg).toInt(),
                        minLevel = minLevel.toInt(),
                        maxLevel = maxLevel.toInt()
                    )
                )
            }
        } else {
            // Fallback: Simulate 5 bands if hardware fails or is not supported
            val simulatedFreqs = listOf(60, 230, 910, 3600, 14000)
            simulatedFreqs.forEachIndexed { index, freq ->
                bandList.add(
                    AppBand(
                        index = index,
                        centerFreq = freq,
                        level = 0,
                        minLevel = -1500,
                        maxLevel = 1500
                    )
                )
            }
            Log.w("AudioEngine", "Hardware EQ not found, using simulated bands")
        }
        
        _bands.value = bandList
    }

    private fun setupPresets() {
        val eq = equalizer
        val presetList = mutableListOf<String>()
        
        if (eq != null && eq.numberOfPresets > 0) {
            for (i in 0 until eq.numberOfPresets) {
                presetList.add(eq.getPresetName(i.toShort()))
            }
        } else {
            presetList.addAll(listOf("Flat", "Bass Boost", "Classical", "Dance", "Folk", "Heavy Metal", "Hip Hop", "Jazz", "Pop", "Rock"))
        }

        _presets.value = presetList
    }

    fun setEnabled(enabled: Boolean) {
        _isEnabled.value = enabled
        equalizer?.enabled = enabled
        bassBoost?.enabled = enabled
        virtualizer?.enabled = enabled
        loudnessEnhancer?.enabled = enabled
    }

    fun setBandLevel(bandIndex: Int, level: Int) {
        // Always apply to hardware if possible
        try {
            equalizer?.setBandLevel(bandIndex.toShort(), level.toShort())
        } catch (e: Exception) { Log.e("AudioEngine", "Set band failed", e) }

        // Always update state for UI
        val currentBands = _bands.value.toMutableList()
        if (bandIndex < currentBands.size) {
            currentBands[bandIndex] = currentBands[bandIndex].copy(level = level)
            _bands.value = currentBands
        }
        _currentPreset.value = -1 // Custom
    }

    fun setPreset(presetIndex: Int) {
        try {
            equalizer?.usePreset(presetIndex.toShort())
        } catch (e: Exception) { Log.e("AudioEngine", "Set preset failed", e) }
        
        _currentPreset.value = presetIndex
        
        // Refresh band levels - if simulated, we might want to manually apply preset curve
        // For simplicity in fallback mode, we just keep current levels or reset to flat
        // If hardware exists, setupEqualizerBands reads from it
        if (equalizer != null) {
            setupEqualizerBands()
        }
    }

    fun setBassLevel(level: Int) {
        _bassLevel.value = level
        try {
            bassBoost?.setStrength(level.toShort())
        } catch (e: Exception) { Log.e("AudioEngine", "Set bass failed", e) }
    }
    
    // Helper: Simulate Bass knob via EQ if BassBoost not strong enough or desired
    fun setBassTreble(bass: Int, treble: Int) {
         // Logic to adjust low-freq bands for bass and high-freq for treble
    }

    fun setVirtualizerLevel(level: Int) {
        _virtualizerLevel.value = level
        try {
            virtualizer?.setStrength(level.toShort())
        } catch (e: Exception) { Log.e("AudioEngine", "Set virt failed", e) }
    }
    
    fun setLoudness(gain: Int) {
        _loudnessLevel.value = gain
        try {
            loudnessEnhancer?.setTargetGain(gain * 10) 
        } catch (e: Exception) { Log.e("AudioEngine", "Set loudness failed", e) }
    }

    fun release() {
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
        loudnessEnhancer?.release()
        
        equalizer = null
        bassBoost = null
        virtualizer = null
        loudnessEnhancer = null
        _isInitialized.value = false
    }
}

data class AppBand(
    val index: Int,
    val centerFreq: Int, // Hz
    val level: Int,
    val minLevel: Int,
    val maxLevel: Int
)
