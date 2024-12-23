package com.litbig.spotify.core.data.datasource.remote

import com.litbig.spotify.core.data.BuildConfig
import com.litbig.spotify.core.data.api.SpotifyApi
import com.litbig.spotify.core.data.api.SpotifyAuthApi
import com.litbig.spotify.core.data.model.remote.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

interface SpotifyDataSource {
    suspend fun search(
        query: String,
        type: String,
        market: String = "KR",
        limit: Int = 10,
        offset: Int = 0,
    ): SearchResponse

    suspend fun getTrackDetails(
        trackId: String,
    ): TrackDetailsResponse

    suspend fun getSeveralTrackDetails(
        trackIds: String,
    ): List<TrackDetailsResponse>

    suspend fun getArtistDetails(
        artistId: String,
    ): ArtistDetailsResponse

    suspend fun getSeveralArtistDetails(
        artistIds: String,
    ): List<ArtistDetailsResponse>

    suspend fun getAlbumsOfArtist(
        artistId: String,
        limit: Int = 10,
        offset: Int = 0,
    ): AlbumsResponse

    suspend fun getTopTracksOfArtist(
        artistId: String,
    ): List<TrackDetailsResponse>

    suspend fun getAlbumDetails(
        albumId: String,
    ): AlbumDetailsResponse

    suspend fun getSeveralAlbumDetails(
        albumIds: String,
    ): List<AlbumDetailsResponse>

    suspend fun getNewAlbumReleases(
        limit: Int = 10,
        offset: Int = 0,
    ): AlbumsResponse?

    suspend fun getPlaylistDetails(
        playlistId: String,
    ): PlaylistDetailsResponse

    suspend fun getUserProfile(
        userId: String,
    ): UserProfileResponse
}

class SpotifyDataSourceImpl @Inject constructor(
    private val authApi: SpotifyAuthApi,
    private val api: SpotifyApi,
) : SpotifyDataSource {

    private var cachedAccessToken: String? = null
    private var tokenExpirationTime: Long = 0
    private val tokenMutex = Mutex()

    private suspend fun getAccessToken(
        clientId: String = BuildConfig.SPOTIFY_ID,
        clientSecret: String = BuildConfig.SPOTIFY_SECRET,
        grantType: String = "client_credentials"
    ): String {
        // 토큰 갱신 필요 여부 확인
        val currentTime = System.currentTimeMillis()
        if (cachedAccessToken != null && currentTime < tokenExpirationTime) {
            return cachedAccessToken ?: throw IllegalStateException("AccessToken is null")
        }

        // 토큰 생성 동기화 처리
        return tokenMutex.withLock {
            // 다른 코루틴이 먼저 토큰을 가져왔는지 재확인
            if (cachedAccessToken != null && currentTime < tokenExpirationTime) {
                return cachedAccessToken ?: throw IllegalStateException("AccessToken is null")
            }

            // 토큰 새로 요청
            val response = authApi.getAccessToken(clientId, clientSecret, grantType)
            cachedAccessToken = "${response.tokenType} ${response.accessToken}"
            tokenExpirationTime = currentTime + response.expiresIn * 1000

            cachedAccessToken ?: throw IllegalStateException("AccessToken is null")
        }
    }

    override suspend fun search(
        query: String,
        type: String,
        market: String,
        limit: Int,
        offset: Int,
    ): SearchResponse {
//        Timber.w("search query=$query, type=$type")
        return api.search(
            query = query,
            type = type,
//            market,
//            limit,
//            offset,
            accessToken = getAccessToken()
        )
    }

    override suspend fun getTrackDetails(
        trackId: String,
    ): TrackDetailsResponse {
        return api.getTrackDetails(trackId, getAccessToken())
    }

    override suspend fun getSeveralTrackDetails(
        trackIds: String,
    ): List<TrackDetailsResponse> {
        return api.getSeveralTrackDetails(trackIds, getAccessToken()).tracks
    }

    override suspend fun getArtistDetails(
        artistId: String,
    ): ArtistDetailsResponse {
        return api.getArtistDetails(artistId, getAccessToken())
    }

    override suspend fun getSeveralArtistDetails(
        artistIds: String,
    ): List<ArtistDetailsResponse> {
        return api.getSeveralArtistDetails(artistIds, getAccessToken()).artists
    }

    override suspend fun getAlbumsOfArtist(
        artistId: String,
        limit: Int,
        offset: Int,
    ): AlbumsResponse {
        return api.getAlbumsOfArtist(
            artistId = artistId,
            limit = limit,
            offset = offset,
            accessToken = getAccessToken()
        )
    }

    override suspend fun getTopTracksOfArtist(
        artistId: String,
    ): List<TrackDetailsResponse> {
        return api.getTopTracksOfArtist(
            artistId = artistId,
            accessToken = getAccessToken()
        ).tracks
    }

    override suspend fun getAlbumDetails(
        albumId: String,
    ): AlbumDetailsResponse {
        return api.getAlbumDetails(albumId, getAccessToken())
    }

    override suspend fun getSeveralAlbumDetails(
        albumIds: String,
    ): List<AlbumDetailsResponse> {
        return api.getSeveralAlbumDetails(albumIds, getAccessToken()).albums
    }

    override suspend fun getNewAlbumReleases(
        limit: Int,
        offset: Int,
    ): AlbumsResponse? {
        return api.getNewAlbumReleases(limit, offset, getAccessToken()).albums
    }

    override suspend fun getPlaylistDetails(playlistId: String): PlaylistDetailsResponse {
        return api.getPlaylistDetails(
            playlistId = playlistId,
            accessToken = getAccessToken()
        )
    }

    override suspend fun getUserProfile(userId: String): UserProfileResponse {
        return api.getUserProfile(
            userId = userId,
            accessToken = getAccessToken()
        )
    }
}