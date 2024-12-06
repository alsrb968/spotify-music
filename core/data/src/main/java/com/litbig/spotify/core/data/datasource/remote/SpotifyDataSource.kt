package com.litbig.spotify.core.data.datasource.remote

import com.litbig.spotify.core.data.api.SpotifyApi
import com.litbig.spotify.core.data.api.SpotifyAuthApi
import com.litbig.spotify.core.data.model.remote.*
import timber.log.Timber
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

    suspend fun getAlbumsOfArtist(
        artistId: String,
        limit: Int = 10,
        offset: Int = 0,
        accessToken: String
    ): AlbumsResponse

    suspend fun getTopTracksOfArtist(
        artistId: String,
        accessToken: String
    ): List<TrackDetailsResponse>

    suspend fun getAlbumDetails(
        albumId: String,
        accessToken: String
    ): AlbumDetailsResponse

    suspend fun getSeveralAlbumDetails(
        albumIds: String,
        accessToken: String
    ): List<AlbumDetailsResponse>

    suspend fun getNewAlbumReleases(
        limit: Int = 10,
        offset: Int = 0,
        accessToken: String
    ): AlbumsResponse?
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
        Timber.w("search query=$query, type=$type")
        return api.search(
            query = query,
            type = type,
//            market,
//            limit,
//            offset,
            accessToken = accessToken
        )
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

    override suspend fun getAlbumsOfArtist(
        artistId: String,
        limit: Int,
        offset: Int,
        accessToken: String
    ): AlbumsResponse {
        return api.getAlbumsOfArtist(
            artistId = artistId,
            limit = limit,
            offset = offset,
            accessToken = accessToken
        )
    }

    override suspend fun getTopTracksOfArtist(
        artistId: String,
        accessToken: String
    ): List<TrackDetailsResponse> {
        return api.getTopTracksOfArtist(
            artistId = artistId,
            accessToken = accessToken
        ).tracks
    }

    override suspend fun getAlbumDetails(
        albumId: String,
        accessToken: String
    ): AlbumDetailsResponse {
        val ret = api.getAlbumDetails(albumId, accessToken)
        Timber.w("getAlbumDetails albumId=$albumId, ret=$ret")
        return ret
    }

    override suspend fun getSeveralAlbumDetails(
        albumIds: String,
        accessToken: String
    ): List<AlbumDetailsResponse> {
        return api.getSeveralAlbumDetails(albumIds, accessToken)
    }

    override suspend fun getNewAlbumReleases(
        limit: Int,
        offset: Int,
        accessToken: String
    ): AlbumsResponse? {
        return api.getNewAlbumReleases(limit, offset, accessToken).albums
    }
}