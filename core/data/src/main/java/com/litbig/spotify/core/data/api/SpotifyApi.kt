package com.litbig.spotify.core.data.api

import com.litbig.spotify.core.data.model.remote.ArtistDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface SpotifyApi {
    @GET("v1/artists/{id}")
    suspend fun getArtistDetails(
        @Path("id") artistId: String,
        @Header("Authorization") accessToken: String
    ): ArtistDetailsResponse
}