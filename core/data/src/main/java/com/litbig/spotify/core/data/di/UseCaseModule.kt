package com.litbig.spotify.core.data.di

import com.litbig.spotify.core.domain.repository.MusicRepository
import com.litbig.spotify.core.domain.usecase.GetAlbumsUseCase
import com.litbig.spotify.core.domain.usecase.GetMetadataUseCase
import com.litbig.spotify.core.domain.usecase.SyncMetadataUseCase
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
    fun provideGetAlbums(
        musicRepository: MusicRepository
    ): GetAlbumsUseCase = GetAlbumsUseCase(
        musicRepository
    )
}