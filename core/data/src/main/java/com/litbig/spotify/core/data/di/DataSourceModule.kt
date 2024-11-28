package com.litbig.spotify.core.data.di

import com.litbig.spotify.core.data.api.SpotifyApi
import com.litbig.spotify.core.data.api.SpotifyAuthApi
import com.litbig.spotify.core.data.datasource.local.MediaRetrieverDataSource
import com.litbig.spotify.core.data.datasource.local.MediaRetrieverDataSourceImpl
import com.litbig.spotify.core.data.datasource.local.RoomMusicDataSource
import com.litbig.spotify.core.data.datasource.local.RoomMusicDataSourceImpl
import com.litbig.spotify.core.data.datasource.remote.SpotifyDataSource
import com.litbig.spotify.core.data.datasource.remote.SpotifyDataSourceImpl
import com.litbig.spotify.core.data.db.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideRoomMusicDataSource(
        musicMetadataDao: MusicMetadataDao,
        albumArtDao: AlbumArtDao,
        artistInfoDao: ArtistInfoDao,
        favoriteDao: FavoriteDao,
        storageHashDao: StorageHashDao,
    ): RoomMusicDataSource = RoomMusicDataSourceImpl(
        musicMetadataDao,
        albumArtDao,
        artistInfoDao,
        favoriteDao,
        storageHashDao,
    )

    @Provides
    @Singleton
    fun provideMediaRetrieverDataSource(): MediaRetrieverDataSource = MediaRetrieverDataSourceImpl()

    @Provides
    @Singleton
    fun provideSpotifyDataSource(
        spotifyAuthApi: SpotifyAuthApi,
        spotifyApi: SpotifyApi
    ): SpotifyDataSource = SpotifyDataSourceImpl(
        spotifyAuthApi,
        spotifyApi
    )
}