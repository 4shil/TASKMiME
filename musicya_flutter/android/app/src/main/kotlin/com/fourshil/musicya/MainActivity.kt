package com.fourshil.musicya

import androidx.annotation.NonNull
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.AndroidEntryPoint
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: FlutterActivity() {
    private val CHANNEL_AUDIO = "com.fourshil.musicya/audio"
    private val CHANNEL_EVENTS = "com.fourshil.musicya/events"

    @Inject
    lateinit var playerController: PlayerController

    private var eventSink: EventChannel.EventSink? = null
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        
        // Method Channel
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_AUDIO).setMethodCallHandler { call, result ->
            when (call.method) {
                "play" -> {
                    playerController.togglePlayPause()
                    result.success(null)
                }
                "pause" -> {
                    playerController.togglePlayPause()
                    result.success(null)
                }
                "skipToNext" -> {
                    playerController.skipToNext()
                    result.success(null)
                }
                "skipToPrevious" -> {
                    playerController.skipToPrevious()
                    result.success(null)
                }
                "seek" -> {
                    val pos = call.argument<Long>("position") ?: 0L
                    playerController.seekTo(pos)
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }

        // Event Channel
        EventChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_EVENTS).setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                eventSink = events
                startObserving()
            }

            override fun onCancel(arguments: Any?) {
                eventSink = null
            }
        })
    }

    private fun startObserving() {
        mainScope.launch {
            // Combine flows or observe individually. For simplicity, we launch separate observers
            // But ideally we emit a combined state.
            // Let's observe key states and emit a map.
            
            launch {
                playerController.currentPosition.collect { pos ->
                   emitState()
                }
            }
            launch {
                playerController.isPlaying.collect { 
                   emitState()
                }
            }
            launch {
                playerController.currentSong.collect { 
                   emitState()
                }
            }
        }
    }

    private fun emitState() {
        val song = playerController.currentSong.value
        val state = mapOf(
            "isPlaying" to playerController.isPlaying.value,
            "position" to playerController.currentPosition.value,
            "duration" to playerController.duration.value,
            "title" to (song?.title ?: ""),
            "artist" to (song?.artist ?: ""),
            "album" to (song?.album ?: ""),
            "uri" to (song?.uri?.toString() ?: "")
        )
        // Post to main thread just in case, though collecting on MainScope
        eventSink?.success(state)
    }
    
    override fun onDestroy() {
        mainScope.cancel()
        super.onDestroy()
    }
}
