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
}
