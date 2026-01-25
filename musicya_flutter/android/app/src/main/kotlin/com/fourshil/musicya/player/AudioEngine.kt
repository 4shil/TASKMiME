package com.fourshil.musicya.player

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.Virtualizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AudioEngine - Complete audio effects processing system.
 * 
 * Manages:
 * - 5-Band Equalizer with presets
 * - Bass Boost (0-1000)
 * - Virtualizer / 3D Surround (0-1000)
 * - Loudness Enhancer / Volume Boost (0-100 dB gain)
 * 
 * All effects are bound to the ExoPlayer's audio session ID.
 */
@Singleton
class AudioEngine @Inject constructor() {
    
    companion object {
        private const val TAG = "AudioEngine"
    }
    
    // Android Audio Effects
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null
    
    private var currentSessionId: Int = 0

    // ========== State Flows for UI ==========
    
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _isEnabled = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()

    private val _bands = MutableStateFlow<List<EqBand>>(emptyList())
    val bands: StateFlow<List<EqBand>> = _bands.asStateFlow()

    private val _presets = MutableStateFlow<List<String>>(emptyList())
    val presets: StateFlow<List<String>> = _presets.asStateFlow()

    private val _currentPresetIndex = MutableStateFlow(-1) // -1 = Custom
    val currentPresetIndex: StateFlow<Int> = _currentPresetIndex.asStateFlow()

    private val _bassStrength = MutableStateFlow(0) // 0-1000
    val bassStrength: StateFlow<Int> = _bassStrength.asStateFlow()

    private val _virtualizerStrength = MutableStateFlow(0) // 0-1000
    val virtualizerStrength: StateFlow<Int> = _virtualizerStrength.asStateFlow()
    
    private val _loudnessGain = MutableStateFlow(0) // 0-100 (represents dB * 10)
    val loudnessGain: StateFlow<Int> = _loudnessGain.asStateFlow()

    // ========== Initialization ==========
    
    /**
     * Attach audio effects to the given audio session.
     * Must be called when ExoPlayer provides a new audio session ID.
     */
    suspend fun attach(sessionId: Int) = withContext(Dispatchers.Default) {
        if (sessionId == 0) {
            Log.w(TAG, "Invalid session ID: 0")
            return@withContext
        }
        
        if (sessionId == currentSessionId && _isInitialized.value) {
            Log.d(TAG, "Already attached to session: $sessionId")
            return@withContext
        }
        
        Log.d(TAG, "Attaching to audio session: $sessionId")
        releaseInternal()
        currentSessionId = sessionId
        
        initializeEqualizer(sessionId)
        initializeBassBoost(sessionId)
        initializeVirtualizer(sessionId)
        initializeLoudnessEnhancer(sessionId)
        
        _isInitialized.value = true
        Log.d(TAG, "Audio engine initialized successfully")
    }
    
    private fun initializeEqualizer(sessionId: Int) {
        try {
            equalizer = Equalizer(0, sessionId).apply {
                enabled = _isEnabled.value
            }
            loadEqualizerBands()
            loadPresets()
            Log.d(TAG, "Equalizer initialized: ${equalizer?.numberOfBands} bands")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Equalizer", e)
            equalizer = null
            // Provide fallback bands for UI
            loadFallbackBands()
            loadFallbackPresets()
        }
    }
    
    private fun initializeBassBoost(sessionId: Int) {
        try {
            bassBoost = BassBoost(0, sessionId).apply {
                enabled = _isEnabled.value
                if (strengthSupported) {
                    setStrength(_bassStrength.value.toShort())
                }
            }
            Log.d(TAG, "BassBoost initialized, strength supported: ${bassBoost?.strengthSupported}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize BassBoost", e)
            bassBoost = null
        }
    }
    
    private fun initializeVirtualizer(sessionId: Int) {
        try {
            virtualizer = Virtualizer(0, sessionId).apply {
                enabled = _isEnabled.value
                if (strengthSupported) {
                    setStrength(_virtualizerStrength.value.toShort())
                }
            }
            Log.d(TAG, "Virtualizer initialized, strength supported: ${virtualizer?.strengthSupported}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Virtualizer", e)
            virtualizer = null
        }
    }
    
    private fun initializeLoudnessEnhancer(sessionId: Int) {
        try {
            loudnessEnhancer = LoudnessEnhancer(sessionId).apply {
                enabled = _isEnabled.value
                setTargetGain(_loudnessGain.value * 10) // Convert to millibels
            }
            Log.d(TAG, "LoudnessEnhancer initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize LoudnessEnhancer (requires API 19+)", e)
            loudnessEnhancer = null
        }
    }
    
    private fun loadEqualizerBands() {
        val eq = equalizer ?: return
        
        val numBands = eq.numberOfBands.toInt()
        val levelRange = eq.bandLevelRange
        val minLevel = levelRange[0].toInt()
        val maxLevel = levelRange[1].toInt()
        
        val bandList = (0 until numBands).map { i ->
            val bandIndex = i.toShort()
            EqBand(
                index = i,
                centerFreqHz = eq.getCenterFreq(bandIndex), // Returns millihertz
                level = eq.getBandLevel(bandIndex).toInt(),
                minLevel = minLevel,
                maxLevel = maxLevel
            )
        }
        
        _bands.value = bandList
    }
    
    private fun loadFallbackBands() {
        // Standard 5-band frequencies in millihertz
        val frequencies = listOf(60_000, 230_000, 910_000, 3_600_000, 14_000_000)
        _bands.value = frequencies.mapIndexed { index, freq ->
            EqBand(
                index = index,
                centerFreqHz = freq,
                level = 0,
                minLevel = -1500,
                maxLevel = 1500
            )
        }
    }
    
    private fun loadPresets() {
        val eq = equalizer ?: return
        val numPresets = eq.numberOfPresets.toInt()
        
        _presets.value = (0 until numPresets).map { i ->
            eq.getPresetName(i.toShort())
        }
    }
    
    private fun loadFallbackPresets() {
        _presets.value = listOf(
            "Flat", "Bass Boost", "Classical", "Dance", 
            "Hip Hop", "Jazz", "Pop", "Rock"
        )
    }

    // ========== EQ Controls ==========
    
    /**
     * Enable or disable all audio effects.
     */
    fun setEnabled(enabled: Boolean) {
        _isEnabled.value = enabled
        equalizer?.enabled = enabled
        bassBoost?.enabled = enabled
        virtualizer?.enabled = enabled
        loudnessEnhancer?.enabled = enabled
        Log.d(TAG, "Audio effects enabled: $enabled")
    }
    
    /**
     * Set a specific band's gain level.
     * @param bandIndex Band index (0 to number of bands - 1)
     * @param level Gain level in millibels (typically -1500 to +1500)
     */
    fun setBandLevel(bandIndex: Int, level: Int) {
        try {
            equalizer?.setBandLevel(bandIndex.toShort(), level.toShort())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set band $bandIndex to $level", e)
        }
        
        // Update state for UI
        _bands.value = _bands.value.toMutableList().apply {
            if (bandIndex in indices) {
                this[bandIndex] = this[bandIndex].copy(level = level)
            }
        }
        _currentPresetIndex.value = -1 // Mark as custom
    }
    
    /**
     * Apply a preset by index.
     */
    fun setPreset(presetIndex: Int) {
        try {
            equalizer?.usePreset(presetIndex.toShort())
            _currentPresetIndex.value = presetIndex
            // Refresh band levels from hardware
            loadEqualizerBands()
            Log.d(TAG, "Applied preset: ${_presets.value.getOrNull(presetIndex)}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply preset $presetIndex", e)
        }
    }
    
    /**
     * Reset EQ to flat (all bands at 0).
     */
    fun resetEqualizer() {
        _bands.value.forEach { band ->
            setBandLevel(band.index, 0)
        }
        _currentPresetIndex.value = -1
    }

    // ========== Bass Boost Controls ==========
    
    /**
     * Set bass boost strength.
     * @param strength Value from 0 (off) to 1000 (maximum)
     */
    fun setBassStrength(strength: Int) {
        val clamped = strength.coerceIn(0, 1000)
        _bassStrength.value = clamped
        try {
            bassBoost?.setStrength(clamped.toShort())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set bass strength", e)
        }
    }

    // ========== Virtualizer Controls ==========
    
    /**
     * Set virtualizer (3D surround) strength.
     * @param strength Value from 0 (off) to 1000 (maximum)
     */
    fun setVirtualizerStrength(strength: Int) {
        val clamped = strength.coerceIn(0, 1000)
        _virtualizerStrength.value = clamped
        try {
            virtualizer?.setStrength(clamped.toShort())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set virtualizer strength", e)
        }
    }

    // ========== Loudness Enhancer Controls ==========
    
    /**
     * Set loudness gain (volume boost).
     * @param gainDb Gain in decibels (0-100). 0 = no boost, 100 = +10dB boost.
     */
    fun setLoudnessGain(gainDb: Int) {
        val clamped = gainDb.coerceIn(0, 100)
        _loudnessGain.value = clamped
        try {
            // LoudnessEnhancer expects millibels (1 dB = 100 mB)
            loudnessEnhancer?.setTargetGain(clamped * 100)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set loudness gain", e)
        }
    }

    // ========== Lifecycle ==========
    
    private fun releaseInternal() {
        try {
            equalizer?.release()
            bassBoost?.release()
            virtualizer?.release()
            loudnessEnhancer?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing effects", e)
        }
        equalizer = null
        bassBoost = null
        virtualizer = null
        loudnessEnhancer = null
        currentSessionId = 0
    }
    
    /**
     * Release all audio effects and resources.
     */
    fun release() {
        releaseInternal()
        _isInitialized.value = false
        Log.d(TAG, "Audio engine released")
    }
}

/**
 * Represents an equalizer band.
 */
data class EqBand(
    val index: Int,
    val centerFreqHz: Int, // Frequency in millihertz (mHz)
    val level: Int,        // Current level in millibels
    val minLevel: Int,     // Minimum level in millibels (usually -1500)
    val maxLevel: Int      // Maximum level in millibels (usually +1500)
) {
    /**
     * Get frequency as formatted string (e.g., "60Hz", "3.6kHz")
     */
    fun formatFrequency(): String {
        val hz = centerFreqHz / 1000 // Convert millihertz to Hz
        return when {
            hz >= 1000 -> "${hz / 1000}kHz"
            else -> "${hz}Hz"
        }
    }
    
    /**
     * Get level as dB string (e.g., "+3dB", "-6dB")
     */
    fun formatLevel(): String {
        val db = level / 100 // Convert millibels to decibels
        return if (db >= 0) "+${db}dB" else "${db}dB"
    }
}
