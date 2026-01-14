package com.fourshil.musicya.player

import android.content.Intent
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import com.fourshil.musicya.ui.widget.MusicWidgetProvider
import dagger.hilt.android.AndroidEntryPoint

import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.LibraryResult
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.fourshil.musicya.data.repository.MusicRepository
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.guava.future
import com.fourshil.musicya.audiofx.AudioEffectController
import javax.inject.Inject

@AndroidEntryPoint
class AudioService : MediaLibraryService() {

    @Inject
    lateinit var player: ExoPlayer

    @Inject
    lateinit var audioEffectController: AudioEffectController
    
    @Inject
    lateinit var repository: MusicRepository

    private var mediaLibrarySession: MediaLibrarySession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Tree Structure Constants
    private val ROOT_ID = "root"
    private val ALL_SONGS_ID = "all_songs"
    private val FOLDERS_ID = "folders"

    override fun onCreate() {
        super.onCreate()
        
        mediaLibrarySession = MediaLibrarySession.Builder(this, player, CustomLibrarySessionCallback())
            .build()
        
        // Initialize Effects
        player.addListener(object : Player.Listener {
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                if (audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
                    audioEffectController.initialize(audioSessionId)
                }
            }
            // Widget Updates
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateWidget()
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateWidget()
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                updateWidget()
            }
        })
        if (player.audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
            audioEffectController.initialize(player.audioSessionId)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when(action) {
                MusicWidgetProvider.ACTION_PLAY_PAUSE -> {
                    if (player.isPlaying) player.pause() else player.play()
                }
                MusicWidgetProvider.ACTION_NEXT -> {
                    if (player.hasNextMediaItem()) player.seekToNext()
                }
                MusicWidgetProvider.ACTION_PREV -> {
                    if (player.hasPreviousMediaItem()) player.seekToPrevious()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateWidget() {
        val title = player.currentMediaItem?.mediaMetadata?.title?.toString() ?: "Musicya"
        val artist = player.currentMediaItem?.mediaMetadata?.artist?.toString() ?: "Select a song"
        val isPlaying = player.isPlaying
        
        MusicWidgetProvider.triggerUpdate(this, isPlaying, title, artist)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onDestroy() {
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        audioEffectController.release()
        super.onDestroy()
    }
    
    private inner class CustomLibrarySessionCallback : MediaLibrarySession.Callback {
        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val rootItem = MediaItem.Builder()
                .setMediaId(ROOT_ID)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setIsBrowsable(true)
                        .setIsPlayable(false)
                        .setTitle("Musicya Library")
                        .build()
                )
                .build()
            return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            
            return serviceScope.future(Dispatchers.IO) {
                val items = mutableListOf<MediaItem>()
                val songs = repository.getSongs() // This might be heavy, cache in production

                when (parentId) {
                    ROOT_ID -> {
                        // Root contains "All Songs" and "Folders"
                        items.add(
                            buildBrowsableItem(ALL_SONGS_ID, "All Songs")
                        )
                        items.add(
                            buildBrowsableItem(FOLDERS_ID, "Folders")
                        )
                    }
                    ALL_SONGS_ID -> {
                        // All Songs flat list
                        songs.forEach { song -> items.add(toMediaItem(song)) }
                    }
                    FOLDERS_ID -> {
                        // List of Unique Folders
                        val folders = songs.map { java.io.File(it.folderPath) }.distinctBy { it.absolutePath }
                        folders.forEach { folder ->
                            items.add(
                                buildBrowsableItem(folder.absolutePath, folder.name)
                            )
                        }
                    }
                    else -> {
                        // Assume ID is a folder path
                        val folderSongs = songs.filter { it.folderPath == parentId }
                        folderSongs.forEach { song -> items.add(toMediaItem(song)) }
                    }
                }
                
                LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
            }
        }
    }

    private fun buildBrowsableItem(id: String, title: String): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setIsBrowsable(true)
                    .setIsPlayable(false)
                    .build()
            )
            .build()
    }

    private fun toMediaItem(song: com.fourshil.musicya.data.model.Song): MediaItem {
        return MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setAlbumTitle(song.album)
                    .setIsBrowsable(false)
                    .setIsPlayable(true)
                    .setExtras(android.os.Bundle().apply { putString("path", song.path) })
                    .build()
            )
            .build()
    }

    // Task removed confirmation helps preventing service not being killed
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player?.playWhenReady == false || player?.mediaItemCount == 0) {
            stopSelf()
        }
    }
}
