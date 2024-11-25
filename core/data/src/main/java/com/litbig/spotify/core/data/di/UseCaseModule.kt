package com.litbig.spotify.core.data.di

import com.litbig.spotify.core.domain.repository.MusicRepository
import com.litbig.spotify.core.domain.usecase.*
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
    fun provideToggleFavoriteUseCase(
        musicRepository: MusicRepository
    ): ToggleFavoriteUseCase = ToggleFavoriteUseCase(
        musicRepository
    )
}