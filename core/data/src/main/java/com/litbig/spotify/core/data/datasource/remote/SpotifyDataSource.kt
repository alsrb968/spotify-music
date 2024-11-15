package com.litbig.spotify.core.data.datasource.remote

import com.litbig.spotify.core.data.api.SpotifyApi
import com.litbig.spotify.core.data.api.SpotifyAuthApi
import com.litbig.spotify.core.data.model.remote.AccessTokenResponse
import com.litbig.spotify.core.data.model.remote.ArtistDetailsResponse
import javax.inject.Inject

interface SpotifyDataSource {
    suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        grantType: String
    ): AccessTokenResponse

    suspend fun getArtistDetails(
        artistId: String,
        accessToken: String
    ): ArtistDetailsResponse
}

class SpotifyDataSourceImpl @Inject constructor(
    private val authApi: SpotifyAuthApi,
    private val api: SpotifyApi,
) : SpotifyDataSource {

    override suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        grantType: String
    ): AccessTokenResponse {
        return authApi.getAccessToken(clientId, clientSecret, grantType)
    }

    override suspend fun getArtistDetails(
        artistId: String,
        accessToken: String
    ): ArtistDetailsResponse {
        return api.getArtistDetails(artistId, accessToken)
    }

}