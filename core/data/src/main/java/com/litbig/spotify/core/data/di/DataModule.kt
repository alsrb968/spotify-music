package com.litbig.spotify.core.data.di

import android.content.Context
import com.litbig.spotify.core.data.api.SpotifyApi
import com.litbig.spotify.core.data.api.SpotifyAuthApi
import com.litbig.spotify.core.data.api.SpotifyClient
import com.litbig.spotify.core.data.datasource.local.MediaRetrieverDataSource
import com.litbig.spotify.core.data.datasource.local.MediaRetrieverDataSourceImpl
import com.litbig.spotify.core.data.datasource.local.RoomMusicDataSource
import com.litbig.spotify.core.data.datasource.local.RoomMusicDataSourceImpl
import com.litbig.spotify.core.data.datasource.remote.SpotifyDataSource
import com.litbig.spotify.core.data.datasource.remote.SpotifyDataSourceImpl
import com.litbig.spotify.core.data.db.AlbumArtDao
import com.litbig.spotify.core.data.db.ArtistInfoDao
import com.litbig.spotify.core.data.db.MusicDatabase
import com.litbig.spotify.core.data.db.MusicMetadataDao
import com.litbig.spotify.core.data.repository.MusicRepositoryImpl
import com.litbig.spotify.core.domain.repository.MusicRepository
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
    fun provideAlbumArtDao(
        database: MusicDatabase
    ): AlbumArtDao = database.albumArtDao()

    @Provides
    @Singleton
    fun provideArtistInfoDao(
        database: MusicDatabase
    ): ArtistInfoDao = database.artistInfoDao()

    @Provides
    @Singleton
    fun provideSpotifyAuthApi(): SpotifyAuthApi =
        SpotifyClient.authRetrofit.create(SpotifyAuthApi::class.java)

    @Provides
    @Singleton
    fun provideSpotifyApi(): SpotifyApi =
        SpotifyClient.retrofit.create(SpotifyApi::class.java)

    @Provides
    @Singleton
    fun provideRoomMusicDataSource(
        musicMetadataDao: MusicMetadataDao,
        albumArtDao: AlbumArtDao,
        artistInfoDao: ArtistInfoDao,
    ): RoomMusicDataSource = RoomMusicDataSourceImpl(
        musicMetadataDao,
        albumArtDao,
        artistInfoDao,
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
    fun provideMusicRepository(
        roomDataSource: RoomMusicDataSource,
        mediaDataSource: MediaRetrieverDataSource,
        spotifyDataSource: SpotifyDataSource,
    ): MusicRepository = MusicRepositoryImpl(
        roomDataSource,
        mediaDataSource,
        spotifyDataSource,
    )
}