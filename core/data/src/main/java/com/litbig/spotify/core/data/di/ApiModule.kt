package com.litbig.spotify.core.data.di

import com.litbig.spotify.core.data.api.SpotifyApi
import com.litbig.spotify.core.data.api.SpotifyAuthApi
import com.litbig.spotify.core.data.api.SpotifyClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideSpotifyAuthApi(): SpotifyAuthApi =
        SpotifyClient.authRetrofit.create(SpotifyAuthApi::class.java)

    @Provides
    @Singleton
    fun provideSpotifyApi(): SpotifyApi =
        SpotifyClient.retrofit.create(SpotifyApi::class.java)
}