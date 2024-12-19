package com.litbig.spotify.core.domain.repository

import com.litbig.spotify.core.domain.model.remote.*
import kotlinx.coroutines.flow.Flow

interface SpotifyRepository {
    suspend fun search(
        query: String,
        type: String,
        market: String = "KR",
        limit: Int = 10,
        offset: Int = 0
    ): Search

    suspend fun searchTrack(trackName: String, artistName: String): TrackDetails?
    suspend fun searchArtist(artistName: String): ArtistDetails?
    suspend fun searchAlbum(albumName: String): AlbumDetails?
    suspend fun searchPlaylistOfArtist(artistName: String): List<PlaylistDetails>?

    suspend fun getTrackDetails(trackId: String): TrackDetails
    suspend fun getSeveralTrackDetails(trackIds: String): List<TrackDetails>
    suspend fun getArtistDetails(artistId: String): ArtistDetails
    suspend fun getSeveralArtistDetails(artistIds: String): List<ArtistDetails>
    suspend fun getAlbumsOfArtist(artistId: String, limit: Int = 10, offset: Int = 0): Albums
    suspend fun getTopTracksOfArtist(artistId: String): List<TrackDetails>
    suspend fun getAlbumDetails(albumId: String): AlbumDetails
    suspend fun getSeveralAlbumDetails(albumIds: String): List<AlbumDetails>
    suspend fun getNewAlbumReleases(limit: Int = 10, offset: Int = 0): Albums?


    suspend fun insertFavorite(id: String, type: String)
    fun isFavorite(id: String, type: String): Flow<Boolean>
    suspend fun deleteFavorite(id: String, type: String)


    fun playTrack(trackId: String)
    fun playTracks(trackIds: List<String>, indexToPlay: Int = 0)
    fun playIndex(index: Int)
    fun addTrack(trackId: String)
    fun addTracks(trackIds: List<String>)
    fun playOrPause()
    fun next()
    fun previous()
    fun seekTo(position: Long)
    fun setShuffle(isShuffle: Boolean)
    fun setRepeat(mode: Int)

    val mediaItems: Flow<List<String>>
    val currentMediaItem: Flow<String?>
    val currentPosition: Flow<Long>
    val playbackState: Flow<Int>
    val isPlaying: Flow<Boolean>
    val isShuffle: Flow<Boolean>
    val repeatMode: Flow<Int>
}