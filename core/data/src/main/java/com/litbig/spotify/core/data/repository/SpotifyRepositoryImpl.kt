package com.litbig.spotify.core.data.repository

import com.litbig.spotify.core.data.datasource.local.PlayerDataSource
import com.litbig.spotify.core.data.datasource.local.RoomMusicDataSource
import com.litbig.spotify.core.data.datasource.remote.SpotifyDataSource
import com.litbig.spotify.core.data.mapper.local.toFavoriteEntity
import com.litbig.spotify.core.data.mapper.remote.*
import com.litbig.spotify.core.domain.model.local.Favorite
import com.litbig.spotify.core.domain.model.remote.*
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SpotifyRepositoryImpl @Inject constructor(
    private val roomDataSource: RoomMusicDataSource,
    private val spotifyDataSource: SpotifyDataSource,
    private val playerDataSource: PlayerDataSource,
) : SpotifyRepository {
    override suspend fun search(
        query: String,
        type: String,
        market: String,
        limit: Int,
        offset: Int
    ): Search {
        return spotifyDataSource.search(query, type, market, limit, offset).toSearch()
    }

    override suspend fun searchTrack(trackName: String, artistName: String): TrackDetails? {
        return spotifyDataSource.search(
            query = "$trackName artist:$artistName",
            type = "track",
        ).let { search ->
            search.tracks?.items?.firstOrNull()?.toTrackDetails()
        }
    }

    override suspend fun searchArtist(artistName: String): ArtistDetails? {
        return spotifyDataSource.search(
            query = artistName,
            type = "artist",
            limit = 1,
        ).let { search ->
            search.artists?.items?.firstOrNull()?.toArtistDetails()
        }
    }

    override suspend fun searchArtists(artistName: String): List<ArtistDetails>? {
        return spotifyDataSource.search(
            query = artistName,
            type = "artist",
        ).let { search ->
            search.artists?.items?.toArtistDetails()
        }
    }

    override suspend fun searchAlbum(albumName: String): AlbumDetails? {
        return spotifyDataSource.search(
            query = albumName,
            type = "album",
        ).let { search ->
            search.albums?.items?.firstOrNull()?.toAlbumDetails()
        }
    }

    override suspend fun searchPlaylistOfArtist(artistName: String): List<PlaylistDetails>? {
        return spotifyDataSource.search(
            query = artistName,
            type = "playlist",
        ).let { search ->
            Timber.d("items: ${search.playlists?.items}")
            search.playlists?.items?.toPlaylistDetails()
        }
    }

    override suspend fun getTrackDetails(trackId: String): TrackDetails {
        return spotifyDataSource.getTrackDetails(trackId).toTrackDetails()
    }

    override suspend fun getSeveralTrackDetails(trackIds: String): List<TrackDetails> {
        return spotifyDataSource.getSeveralTrackDetails(trackIds).toTrackDetails()
    }

    override suspend fun getArtistDetails(artistId: String): ArtistDetails {
        return spotifyDataSource.getArtistDetails(artistId).toArtistDetails()
    }

    override suspend fun getSeveralArtistDetails(artistIds: String): List<ArtistDetails> {
        return spotifyDataSource.getSeveralArtistDetails(artistIds).toArtistDetails()
    }

    override suspend fun getAlbumsOfArtist(artistId: String, limit: Int, offset: Int): Albums {
        return spotifyDataSource.getAlbumsOfArtist(artistId, limit, offset).toAlbums()
    }

    override suspend fun getTopTracksOfArtist(artistId: String): List<TrackDetails> {
        return spotifyDataSource.getTopTracksOfArtist(artistId).toTrackDetails()
    }

    override suspend fun getAlbumDetails(albumId: String): AlbumDetails {
        return spotifyDataSource.getAlbumDetails(albumId).toAlbumDetails()
    }

    override suspend fun getSeveralAlbumDetails(albumIds: String): List<AlbumDetails> {
        return spotifyDataSource.getSeveralAlbumDetails(albumIds).toAlbumDetails()
    }

    override suspend fun getNewAlbumReleases(limit: Int, offset: Int): Albums? {
        return spotifyDataSource.getNewAlbumReleases(limit, offset)?.toAlbums()
    }

    override suspend fun insertFavorite(id: String, type: String) {
        val favorite = Favorite(
            name = id,
            type = type,
            imageUrl = null,
        ).toFavoriteEntity()
        roomDataSource.insertFavorite(favorite)
    }

    override fun isFavorite(id: String, type: String): Flow<Boolean> {
        return roomDataSource.isFavorite(id, type)
    }

    override suspend fun deleteFavorite(id: String, type: String) {
        roomDataSource.deleteFavorite(id, type)
    }

    override fun playTrack(trackId: String) {
        playerDataSource.play(trackId)
    }

    override fun playTracks(trackIds: List<String>, indexToPlay: Int) {
        playerDataSource.play(trackIds, indexToPlay)
    }

    override fun playIndex(index: Int) {
        playerDataSource.playIndex(index)
    }

    override fun addTrack(trackId: String) {
        playerDataSource.addPlayList(trackId)
    }

    override fun addTracks(trackIds: List<String>) {
        playerDataSource.addPlayLists(trackIds)
    }

    override fun playOrPause() {
        CoroutineScope(Dispatchers.IO).launch {
            if (playerDataSource.isPlaying.first()) {
                playerDataSource.pause()
            } else {
                playerDataSource.resume()
            }
        }
    }

    override fun next() {
        playerDataSource.next()
    }

    override fun previous() {
        playerDataSource.previous()
    }

    override fun seekTo(position: Long) {
        playerDataSource.seekTo(position)
    }

    override fun setShuffle(isShuffle: Boolean) {
        playerDataSource.setShuffle(isShuffle)
    }

    override fun setRepeat(mode: Int) {
        playerDataSource.setRepeat(mode)
    }

    override val mediaItems: Flow<List<String>>
        get() = playerDataSource.mediaItems

    override val currentMediaItem: Flow<String?>
        get() = playerDataSource.currentMediaItem

    override val currentPosition: Flow<Long>
        get() = playerDataSource.currentPosition

    override val playbackState: Flow<Int>
        get() = playerDataSource.playbackState

    override val isPlaying: Flow<Boolean>
        get() = playerDataSource.isPlaying

    override val isShuffle: Flow<Boolean>
        get() = playerDataSource.isShuffle

    override val repeatMode: Flow<Int>
        get() = playerDataSource.repeatMode
}