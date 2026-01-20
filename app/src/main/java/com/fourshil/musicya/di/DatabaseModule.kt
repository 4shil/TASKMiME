package com.fourshil.musicya.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fourshil.musicya.data.db.AppDatabase
import com.fourshil.musicya.data.db.MusicDao
import com.fourshil.musicya.data.repository.IMusicRepository
import com.fourshil.musicya.data.repository.MusicRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Migration from version 1 to 2.
     * v2 added SongPlayHistory table for play tracking.
     */
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create SongPlayHistory table if it doesn't exist
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `song_play_history` (
                    `songId` INTEGER NOT NULL PRIMARY KEY,
                    `playCount` INTEGER NOT NULL DEFAULT 0,
                    `lastPlayedAt` INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )
        }
    }
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "lyra_database"
        )
        .addMigrations(MIGRATION_1_2)
        .build()
    }
    
    @Provides
    @Singleton
    fun provideMusicDao(database: AppDatabase): MusicDao {
        return database.musicDao()
    }
}

/**
 * Hilt module to bind IMusicRepository interface to MusicRepository implementation.
 * This enables easy swapping for testing with fake implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindMusicRepository(impl: MusicRepository): IMusicRepository
}
