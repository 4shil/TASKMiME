package com.fourshil.musicya.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for music-related database operations.
 */
@Dao
interface MusicDao {
    
    // ============ FAVORITES ============
    
    @Query("SELECT * FROM favorite_songs ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteSong>>
    
    @Query("SELECT songId FROM favorite_songs")
    fun getFavoriteIds(): Flow<List<Long>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_songs WHERE songId = :songId)")
    fun isFavorite(songId: Long): Flow<Boolean>
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_songs WHERE songId = :songId)")
    suspend fun isFavoriteSync(songId: Long): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteSong)
    
    @Query("DELETE FROM favorite_songs WHERE songId = :songId")
    suspend fun removeFavorite(songId: Long)
    
    @Transaction
    suspend fun toggleFavorite(songId: Long) {
        if (isFavoriteSync(songId)) {
            removeFavorite(songId)
        } else {
            addFavorite(FavoriteSong(songId))
        }
    }
    
    // ============ PLAYLISTS ============
    
    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>
    
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylist(playlistId: Long): Playlist?
    
    @Insert
    suspend fun createPlaylist(playlist: Playlist): Long
    
    @Update
    suspend fun updatePlaylist(playlist: Playlist)
    
    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)
    
    @Query("UPDATE playlists SET name = :name, updatedAt = :updatedAt WHERE id = :playlistId")
    suspend fun renamePlaylist(playlistId: Long, name: String, updatedAt: Long = System.currentTimeMillis())
    
    // ============ PLAYLIST SONGS ============
    
    @Query("SELECT songId FROM playlist_songs WHERE playlistId = :playlistId ORDER BY sortOrder")
    fun getPlaylistSongIds(playlistId: Long): Flow<List<Long>>
    
    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY sortOrder")
    fun getPlaylistSongs(playlistId: Long): Flow<List<PlaylistSong>>
    
    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
    fun getPlaylistSongCount(playlistId: Long): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(playlistSong: PlaylistSong)
    
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)
    
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)
    
    @Transaction
    suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {
        var order = 0
        songIds.forEach { songId ->
            addSongToPlaylist(PlaylistSong(playlistId, songId, sortOrder = order++))
        }
        // Update playlist timestamp
        getPlaylist(playlistId)?.let {
            updatePlaylist(it.copy(updatedAt = System.currentTimeMillis()))
        }
    }
    
    // ============ PLAY HISTORY ============
    
    /**
     * Record that a song was played. Increments play count and updates last played time.
     */
    @Transaction
    suspend fun recordPlay(songId: Long) {
        val existing = getPlayHistory(songId)
        if (existing != null) {
            updatePlayHistory(SongPlayHistory(
                songId = songId,
                playCount = existing.playCount + 1,
                lastPlayedAt = System.currentTimeMillis()
            ))
        } else {
            insertPlayHistory(SongPlayHistory(
                songId = songId,
                playCount = 1,
                lastPlayedAt = System.currentTimeMillis()
            ))
        }
    }
    
    @Query("SELECT * FROM song_play_history WHERE songId = :songId")
    suspend fun getPlayHistory(songId: Long): SongPlayHistory?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayHistory(history: SongPlayHistory)
    
    @Update
    suspend fun updatePlayHistory(history: SongPlayHistory)
    
    /**
     * Get song IDs ordered by play count (most played first).
     * @param limit Maximum number of results
     */
    @Query("SELECT songId FROM song_play_history WHERE playCount > 0 ORDER BY playCount DESC LIMIT :limit")
    fun getMostPlayedSongIds(limit: Int = 50): Flow<List<Long>>
    
    /**
     * Get song IDs ordered by last played time (most recent first).
     * @param limit Maximum number of results
     */
    @Query("SELECT songId FROM song_play_history WHERE lastPlayedAt > 0 ORDER BY lastPlayedAt DESC LIMIT :limit")
    fun getRecentlyPlayedSongIds(limit: Int = 50): Flow<List<Long>>
    
    /**
     * Get play count for a specific song.
     */
    @Query("SELECT playCount FROM song_play_history WHERE songId = :songId")
    fun getPlayCount(songId: Long): Flow<Int?>

    /**
     * Get IDs of all songs that have been played at least once.
     * Used to calculate "Never Played" songs.
     */
    @Query("SELECT songId FROM song_play_history WHERE playCount > 0")
    fun getAllPlayedSongIds(): Flow<List<Long>>
}

