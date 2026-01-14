package com.fourshil.musicya.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Query("SELECT * FROM blacklisted_paths")
    fun getBlacklistedPaths(): Flow<List<BlacklistedPath>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlacklistedPath(path: BlacklistedPath)

    @Delete
    suspend fun deleteBlacklistedPath(path: BlacklistedPath)
}

@Database(entities = [BlacklistedPath::class], version = 1, exportSchema = false)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao
}
