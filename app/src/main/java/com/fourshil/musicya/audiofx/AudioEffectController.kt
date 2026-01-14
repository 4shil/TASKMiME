package com.fourshil.musicya.audiofx

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioEffectController @Inject constructor(
    private val repository: AudioFxRepository
) {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    
    private var scope = CoroutineScope(Dispatchers.Main)
    private var observingJob: Job? = null

    fun initialize(audioSessionId: Int) {
        release()
        
        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = repository.equalizerEnabled.value
            }
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = true
            }
            virtualizer = Virtualizer(0, audioSessionId).apply {
                enabled = true
            }
            
            // Initialize Repository bands if empty (first run)
            if (repository.equalizerBands.value.isEmpty()) {
                val bands = mutableListOf<EqualizerBand>()
                val numBands = equalizer?.numberOfBands ?: 0
                for (i in 0 until numBands) {
                    val idx = i.toShort()
                    bands.add(
                        EqualizerBand(
                            index = idx,
                            centerFreq = equalizer?.getCenterFreq(idx) ?: 0,
                            level = equalizer?.getBandLevel(idx) ?: 0
                        )
                    )
                }
                repository.updateBands(bands)
            }
            
            startObserving()
            
        } catch (e: Exception) {
            Log.e("AudioEffectController", "Error initializing effects", e)
        }
    }

    private fun startObserving() {
        observingJob = scope.launch {
            launch {
                repository.bassBoostStrength.collect { strength ->
                    bassBoost?.setStrength(strength)
                }
            }
            launch {
                repository.virtualizerStrength.collect { strength ->
                    virtualizer?.setStrength(strength)
                }
            }
            launch {
                repository.equalizerEnabled.collect { enabled ->
                    equalizer?.enabled = enabled
                }
            }
            launch {
                repository.equalizerBands.collect { bands ->
                    bands.forEach { band ->
                        equalizer?.setBandLevel(band.index, band.level)
                    }
                }
            }
        }
    }

    fun release() {
        observingJob?.cancel()
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
        equalizer = null
        bassBoost = null
        virtualizer = null
    }
}
