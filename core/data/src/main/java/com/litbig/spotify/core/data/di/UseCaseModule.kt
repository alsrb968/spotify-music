package com.litbig.spotify.core.data.di

import com.litbig.spotify.core.domain.repository.MusicRepository
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import com.litbig.spotify.core.domain.repository.StorageRepository
import com.litbig.spotify.core.domain.usecase.*
import com.litbig.spotify.core.domain.usecase.favorite.*
import com.litbig.spotify.core.domain.usecase.metadata.*
import com.litbig.spotify.core.domain.usecase.spotify.*
import com.litbig.spotify.core.domain.usecase.storage.AddStorageHashUseCase
import com.litbig.spotify.core.domain.usecase.storage.GetStorageHashUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideSyncMusicMetadataUseCase(repository: MusicRepository) =
        SyncMetadataUseCase(repository)

    @Provides
    @Singleton
    fun provideGetMetadataUseCase(repository: MusicRepository) =
        GetMetadataUseCase(repository)

    @Provides
    @Singleton
    fun provideGetMetadataByAlbumUseCase(repository: MusicRepository) =
        GetMetadataByAlbumUseCase(repository)

    @Provides
    @Singleton
    fun provideGetMetadataByArtistUseCase(repository: MusicRepository) =
        GetMetadataByArtistUseCase(repository)

    @Provides
    @Singleton
    fun provideGetAlbumsUseCase(repository: MusicRepository) =
        GetAlbumsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetArtistsUseCase(repository: MusicRepository) =
        GetArtistsUseCase(repository)


    // --- Favorite ---
    @Provides
    @Singleton
    fun provideGetFavoritesUseCase(repository: MusicRepository) =
        GetFavoritesUseCase(repository)

    @Provides
    @Singleton
    fun provideIsFavoriteUseCase(repository: MusicRepository) =
        IsFavoriteUseCase(repository)

    @Provides
    @Singleton
    fun provideToggleFavoriteUseCase(repository: MusicRepository) =
        ToggleFavoriteUseCase(repository)


    // --- Storage ---
    @Provides
    @Singleton
    fun provideAddStorageHashUseCase(repository: StorageRepository) =
        AddStorageHashUseCase(repository)

    @Provides
    @Singleton
    fun provideGetStorageHashUseCase(repository: StorageRepository) =
        GetStorageHashUseCase(repository)


    // --- Spotify ---
    @Provides
    @Singleton
    fun provideSearchUseCase(repository: SpotifyRepository) =
        SearchUseCase(repository)

    @Provides
    @Singleton
    fun provideGetNewAlbumReleasesUseCase(repository: SpotifyRepository) =
        GetNewAlbumReleasesUseCase(repository)

    @Provides
    @Singleton
    fun provideGetArtistDetailsUseCase(repository: SpotifyRepository) =
        GetArtistDetailsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetSeveralArtistDetailsUseCase(repository: SpotifyRepository) =
        GetSeveralArtistDetailsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetAlbumDetailsUseCase(repository: SpotifyRepository) =
        GetAlbumDetailsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetSeveralTrackDetailsUseCase(repository: SpotifyRepository) =
        GetSeveralTrackDetailsUseCase(repository)

    @Provides
    @Singleton
    fun provideSearchArtistUseCase(repository: SpotifyRepository) =
        SearchArtistUseCase(repository)

    @Provides
    @Singleton
    fun provideGetAlbumDetailsListOfArtistsUseCase(repository: SpotifyRepository) =
        GetAlbumDetailsListOfArtistsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetTopTrackDetailsListOfArtistsUseCase(repository: SpotifyRepository) =
        GetTopTrackDetailsListOfArtistsUseCase(repository)
}