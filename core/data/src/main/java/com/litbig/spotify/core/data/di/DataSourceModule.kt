package com.litbig.spotify.core.data.di

import androidx.media3.exoplayer.ExoPlayer
import com.litbig.spotify.core.data.api.SpotifyApi
import com.litbig.spotify.core.data.api.SpotifyAuthApi
import com.litbig.spotify.core.data.datasource.local.*
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

    @Provides
    @Singleton
    fun providePlayerDataSource(
        exoPlayer: ExoPlayer
    ): PlayerDataSource = PlayerDataSourceImpl(
        exoPlayer
    )
}