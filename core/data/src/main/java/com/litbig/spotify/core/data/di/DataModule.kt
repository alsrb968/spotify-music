package com.litbig.spotify.core.data.di

import android.content.Context
import com.litbig.spotify.core.data.datasource.local.MediaRetrieverDataSource
import com.litbig.spotify.core.data.datasource.local.MediaRetrieverDataSourceImpl
import com.litbig.spotify.core.data.datasource.local.RoomMusicDataSource
import com.litbig.spotify.core.data.datasource.local.RoomMusicDataSourceImpl
import com.litbig.spotify.core.data.db.MusicDatabase
import com.litbig.spotify.core.data.db.MusicMetadataDao
import com.litbig.spotify.core.data.repository.MusicRepositoryImpl
import com.litbig.spotify.core.domain.repository.MusicRepository
import com.litbig.spotify.core.domain.usecase.SyncMetadataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MusicDatabase = MusicDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideMusicMetadataDao(
        database: MusicDatabase
    ): MusicMetadataDao = database.musicMetadataDao()

    @Provides
    @Singleton
    fun provideRoomMusicDataSource(
        musicMetadataDao: MusicMetadataDao
    ): RoomMusicDataSource = RoomMusicDataSourceImpl(
        musicMetadataDao
    )

    @Provides
    @Singleton
    fun provideMediaRetrieverDataSource(): MediaRetrieverDataSource = MediaRetrieverDataSourceImpl()

    @Provides
    @Singleton
    fun provideMusicRepository(
        roomDataSource: RoomMusicDataSource,
        mediaDataSource: MediaRetrieverDataSource
    ): MusicRepository = MusicRepositoryImpl(
        roomDataSource,
        mediaDataSource
    )
}