package com.litbig.spotify.core.data.di

import com.litbig.spotify.core.domain.repository.MusicRepository
import com.litbig.spotify.core.domain.repository.StorageRepository
import com.litbig.spotify.core.domain.usecase.*
import com.litbig.spotify.core.domain.usecase.favorite.*
import com.litbig.spotify.core.domain.usecase.metadata.*
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
    fun provideSyncMusicMetadataUseCase(
        musicRepository: MusicRepository
    ): SyncMetadataUseCase = SyncMetadataUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideGetMetadataUseCase(
        musicRepository: MusicRepository
    ): GetMetadataUseCase = GetMetadataUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideGetMetadataByAlbumUseCase(
        musicRepository: MusicRepository
    ): GetMetadataByAlbumUseCase = GetMetadataByAlbumUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideGetMetadataByArtistUseCase(
        musicRepository: MusicRepository
    ): GetMetadataByArtistUseCase = GetMetadataByArtistUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideGetAlbumsUseCase(
        musicRepository: MusicRepository
    ): GetAlbumsUseCase = GetAlbumsUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideGetArtistsUseCase(
        musicRepository: MusicRepository
    ): GetArtistsUseCase = GetArtistsUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideSearchUseCase(
        musicRepository: MusicRepository
    ): SearchUseCase = SearchUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideGetNewAlbumReleasesUseCase(
        musicRepository: MusicRepository
    ): GetNewAlbumReleasesUseCase = GetNewAlbumReleasesUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideGetArtistRelatedInfoUseCase(
        musicRepository: MusicRepository
    ): GetArtistRelatedInfoUseCase = GetArtistRelatedInfoUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideGetAlbumDetailsUseCase(
        musicRepository: MusicRepository
    ): GetAlbumDetailsUseCase = GetAlbumDetailsUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideGetTrackDetailsUseCase(
        musicRepository: MusicRepository
    ): GetTrackDetailsUseCase = GetTrackDetailsUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideSearchArtistUseCase(
        musicRepository: MusicRepository
    ): SearchArtistUseCase = SearchArtistUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideGetFavoritesUseCase(
        musicRepository: MusicRepository
    ): GetFavoritesUseCase = GetFavoritesUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideIsFavoriteUseCase(
        musicRepository: MusicRepository
    ): IsFavoriteUseCase = IsFavoriteUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideToggleFavoriteUseCase(
        musicRepository: MusicRepository
    ): ToggleFavoriteUseCase = ToggleFavoriteUseCase(
        musicRepository
    )

    @Provides
    @Singleton
    fun provideAddStorageHashUseCase(
        storageRepository: StorageRepository
    ): AddStorageHashUseCase = AddStorageHashUseCase(
        storageRepository
    )

    @Provides
    @Singleton
    fun provideGetStorageHashUseCase(
        storageRepository: StorageRepository
    ): GetStorageHashUseCase = GetStorageHashUseCase(
        storageRepository
    )
}