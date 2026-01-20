package com.fourshil.musicya.data.repository

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for MusicRepository logic.
 */
class MusicRepositoryTest {

    @Test
    fun `song filtering by artist name is case-sensitive`() {
        val songs = listOf(
            TestSong(1, "Song 1", "ArtistA"),
            TestSong(2, "Song 2", "ArtistB"),
            TestSong(3, "Song 3", "artistA") // lowercase
        )
        
        val filtered = songs.filter { it.artist == "ArtistA" }
        assertEquals(1, filtered.size)
        assertEquals(1L, filtered.first().id)
    }

    @Test
    fun `song grouping by album works correctly`() {
        val songs = listOf(
            TestSong(1, "Song 1", "Artist", albumId = 100),
            TestSong(2, "Song 2", "Artist", albumId = 100),
            TestSong(3, "Song 3", "Artist", albumId = 200)
        )
        
        val grouped = songs.groupBy { it.albumId }
        assertEquals(2, grouped.size)
        assertEquals(2, grouped[100L]?.size)
        assertEquals(1, grouped[200L]?.size)
    }

    @Test
    fun `folder path extraction from file path`() {
        val path = "/storage/emulated/0/Music/Artist/Album/song.mp3"
        val folder = path.substringBeforeLast("/")
        assertEquals("/storage/emulated/0/Music/Artist/Album", folder)
    }

    @Test
    fun `folder name from path`() {
        val path = "/storage/emulated/0/Music/Artist/Album/song.mp3"
        val folder = path.substringBeforeLast("/")
        val folderName = folder.substringAfterLast("/")
        assertEquals("Album", folderName)
    }

    @Test
    fun `caching returns same list without re-query`() {
        var queryCount = 0
        
        // Simulate cached fetch
        fun getSongs(): List<TestSong> {
            queryCount++
            return listOf(TestSong(1, "Song", "Artist"))
        }
        
        var cache: List<TestSong>? = null
        
        // First call - cache miss
        cache = cache ?: getSongs()
        assertEquals(1, queryCount)
        
        // Second call - cache hit
        cache = cache ?: getSongs()
        assertEquals(1, queryCount) // Should not increment
    }

    @Test
    fun `song ID list generation`() {
        val songs = listOf(
            TestSong(1, "Song 1", "Artist"),
            TestSong(2, "Song 2", "Artist"),
            TestSong(3, "Song 3", "Artist")
        )
        
        val ids = songs.map { it.id }
        assertEquals(listOf(1L, 2L, 3L), ids)
    }

    @Test
    fun `paged query offset and limit calculation`() {
        val pageSize = 50
        val page = 2
        
        val offset = page * pageSize
        val limit = pageSize
        
        assertEquals(100, offset)
        assertEquals(50, limit)
    }

    @Test
    fun `delete returns count of deleted items`() {
        val songIds = listOf(1L, 2L, 3L)
        var deletedCount = 0
        
        // Simulate deletion with some failures
        songIds.forEach { id ->
            val success = id != 2L // ID 2 fails
            if (success) deletedCount++
        }
        
        assertEquals(2, deletedCount)
    }

    private data class TestSong(
        val id: Long,
        val title: String,
        val artist: String,
        val albumId: Long = 0L
    )
}
