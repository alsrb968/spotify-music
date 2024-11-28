package com.litbig.spotify.core.data.di

import android.content.Context
import com.litbig.spotify.core.data.db.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {
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
    fun provideFavoriteDao(
        database: MusicDatabase
    ): FavoriteDao = database.favoriteDao()

    @Provides
    @Singleton
    fun provideStorageHashDao(
        database: MusicDatabase
    ): StorageHashDao = database.storageHashDao()
}