package com.litbig.spotify.core.data.datasource.remote

import com.litbig.spotify.core.data.api.SpotifyApi
import com.litbig.spotify.core.data.api.SpotifyAuthApi
import com.litbig.spotify.core.data.model.remote.*
import javax.inject.Inject

interface SpotifyDataSource {
    suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        grantType: String
    ): AccessTokenResponse

    suspend fun search(
        query: String,
        type: String,
        market: String = "KR",
        limit: Int = 10,
        offset: Int = 0,
        accessToken: String
    ): SearchResponse

    suspend fun getTrackDetails(
        trackId: String,
        accessToken: String
    ): TrackDetailsResponse

    suspend fun getSeveralTrackDetails(
        trackIds: String,
        accessToken: String
    ): List<TrackDetailsResponse>

    suspend fun getArtistDetails(
        artistId: String,
        accessToken: String
    ): ArtistDetailsResponse

    suspend fun getSeveralArtistDetails(
        artistIds: String,
        accessToken: String
    ): List<ArtistDetailsResponse>

    suspend fun getAlbumDetails(
        albumId: String,
        accessToken: String
    ): AlbumDetailsResponse

    suspend fun getSeveralAlbumDetails(
        albumIds: String,
        accessToken: String
    ): List<AlbumDetailsResponse>
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

    override suspend fun search(
        query: String,
        type: String,
        market: String,
        limit: Int,
        offset: Int,
        accessToken: String
    ): SearchResponse {
        return api.search(query, type, market, limit, offset, accessToken)
    }

    override suspend fun getTrackDetails(
        trackId: String,
        accessToken: String
    ): TrackDetailsResponse {
        return api.getTrackDetails(trackId, accessToken)
    }

    override suspend fun getSeveralTrackDetails(
        trackIds: String,
        accessToken: String
    ): List<TrackDetailsResponse> {
        return api.getSeveralTrackDetails(trackIds, accessToken)
    }

    override suspend fun getArtistDetails(
        artistId: String,
        accessToken: String
    ): ArtistDetailsResponse {
        return api.getArtistDetails(artistId, accessToken)
    }

    override suspend fun getSeveralArtistDetails(
        artistIds: String,
        accessToken: String
    ): List<ArtistDetailsResponse> {
        return api.getSeveralArtistDetails(artistIds, accessToken)
    }

    override suspend fun getAlbumDetails(
        albumId: String,
        accessToken: String
    ): AlbumDetailsResponse {
        return api.getAlbumDetails(albumId, accessToken)
    }

    override suspend fun getSeveralAlbumDetails(
        albumIds: String,
        accessToken: String
    ): List<AlbumDetailsResponse> {
        return api.getSeveralAlbumDetails(albumIds, accessToken)
    }
}