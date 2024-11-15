package com.litbig.spotify.core.data.api

import com.litbig.spotify.core.data.model.remote.AccessTokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SpotifyAuthApi {
    @FormUrlEncoded
    @POST("api/token")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String,
    ): AccessTokenResponse
}