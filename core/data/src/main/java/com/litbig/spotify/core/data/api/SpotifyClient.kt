package com.litbig.spotify.core.data.api

import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SpotifyClient {
    private val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
        .build()
    private val client = OkHttpClient.Builder()
        .connectionSpecs(listOf(spec))
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private const val BASE_URL = "https://api.spotify.com"
    private var _retrofit: Retrofit? = null

    val retrofit: Retrofit =
        _retrofit ?: synchronized(this) {
            _retrofit ?: Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().also {
                    _retrofit = it
                }
        }

    private const val AUTH_BASE_URL = "https://accounts.spotify.com"
    private var _authRetrofit: Retrofit? = null

    val authRetrofit: Retrofit =
        _authRetrofit ?: synchronized(this) {
            _authRetrofit ?: Retrofit.Builder()
                .baseUrl(AUTH_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().also {
                    _authRetrofit = it
                }
        }
}