package com.litbig.spotify.core.data.di

import com.litbig.spotify.core.data.datasource.local.MediaRetrieverDataSource
import com.litbig.spotify.core.data.datasource.local.RoomMusicDataSource
import com.litbig.spotify.core.data.datasource.remote.SpotifyDataSource
import com.litbig.spotify.core.data.repository.MusicRepositoryImpl
import com.litbig.spotify.core.data.repository.StorageHashRepositoryImpl
import com.litbig.spotify.core.domain.repository.MusicRepository
import com.litbig.spotify.core.domain.repository.StorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideMusicRepository(
        roomDataSource: RoomMusicDataSource,
        mediaDataSource: MediaRetrieverDataSource,
        spotifyDataSource: SpotifyDataSource,
    ): MusicRepository = MusicRepositoryImpl(
        roomDataSource,
        mediaDataSource,
        spotifyDataSource,
    )

    @Provides
    @Singleton
    fun provideStorageRepository(
        roomDataSource: RoomMusicDataSource,
    ): StorageRepository = StorageHashRepositoryImpl(
        roomDataSource,
    )
}