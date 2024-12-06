package com.litbig.spotify.core.data.api

import com.litbig.spotify.core.data.model.remote.*
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApi {
    @GET("v1/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("market") market: String = "KR",
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0,
        @Header("Authorization") accessToken: String
    ): SearchResponse

    @GET("v1/tracks/{id}")
    suspend fun getTrackDetails(
        @Path("id") trackId: String,
        @Header("Authorization") accessToken: String
    ): TrackDetailsResponse

    @GET("v1/tracks")
    suspend fun getSeveralTrackDetails(
        @Query("ids") trackIds: String,
        @Header("Authorization") accessToken: String
    ): List<TrackDetailsResponse>

    @GET("v1/artists/{id}")
    suspend fun getArtistDetails(
        @Path("id") artistId: String,
        @Header("Authorization") accessToken: String
    ): ArtistDetailsResponse

    @GET("v1/artists")
    suspend fun getSeveralArtistDetails(
        @Query("ids") artistIds: String,
        @Header("Authorization") accessToken: String
    ): List<ArtistDetailsResponse>

    @GET("v1/artists/{id}/albums")
    suspend fun getAlbumsOfArtist(
        @Path("id") artistId: String,
//        @Query("include_groups") includeGroups: String,
        @Query("market") market: String = "KR",
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Header("Authorization") accessToken: String
    ): AlbumsResponse

    @GET("v1/artists/{id}/top-tracks")
    suspend fun getTopTracksOfArtist(
        @Path("id") artistId: String,
        @Query("market") market: String = "KR",
        @Header("Authorization") accessToken: String
    ): TopTracksResponse

    @GET("v1/albums/{id}")
    suspend fun getAlbumDetails(
        @Path("id") albumId: String,
        @Header("Authorization") accessToken: String
    ): AlbumDetailsResponse

    @GET("v1/albums")
    suspend fun getSeveralAlbumDetails(
        @Query("ids") albumIds: String,
        @Header("Authorization") accessToken: String
    ): List<AlbumDetailsResponse>

    @GET("v1/browse/new-releases")
    suspend fun getNewAlbumReleases(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Header("Authorization") accessToken: String
    ): SearchResponse
}