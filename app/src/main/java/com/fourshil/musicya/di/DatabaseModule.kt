package com.fourshil.musicya.di

import android.content.Context
import androidx.room.Room
import com.fourshil.musicya.data.local.MusicDao
import com.fourshil.musicya.data.local.MusicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MusicDatabase =
        Room.databaseBuilder(
            context,
            MusicDatabase::class.java,
            "musicya_db"
        ).build()

    @Provides
    fun provideMusicDao(database: MusicDatabase): MusicDao = database.musicDao()
}
