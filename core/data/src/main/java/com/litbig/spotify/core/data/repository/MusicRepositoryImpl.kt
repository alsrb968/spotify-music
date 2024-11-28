package com.litbig.spotify.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.litbig.spotify.core.data.BuildConfig
import com.litbig.spotify.core.data.datasource.local.MediaRetrieverDataSource
import com.litbig.spotify.core.data.datasource.local.RoomMusicDataSource
import com.litbig.spotify.core.data.datasource.remote.SpotifyDataSource
import com.litbig.spotify.core.data.mapper.local.*
import com.litbig.spotify.core.data.mapper.remote.toAlbumDetails
import com.litbig.spotify.core.data.mapper.remote.toArtistDetails
import com.litbig.spotify.core.data.mapper.remote.toSearch
import com.litbig.spotify.core.data.mapper.remote.toTrackDetails
import com.litbig.spotify.core.domain.model.local.AlbumArt
import com.litbig.spotify.core.domain.model.local.ArtistInfo
import com.litbig.spotify.core.domain.model.local.Favorite
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.core.domain.model.remote.AlbumDetails
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.model.remote.Search
import com.litbig.spotify.core.domain.model.remote.TrackDetails
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val roomDataSource: RoomMusicDataSource,
    private val mediaDataSource: MediaRetrieverDataSource,
    private val spotifyDataSource: SpotifyDataSource,
) : MusicRepository {

    private var cachedAccessToken: String? = null
    private var tokenExpirationTime: Long = 0

    private suspend fun getAccessToken(): String {
        val currentTime = System.currentTimeMillis()
        if (cachedAccessToken == null || currentTime >= tokenExpirationTime) {
            val response = spotifyDataSource.getAccessToken(
                clientId = BuildConfig.SPOTIFY_ID,
                clientSecret = BuildConfig.SPOTIFY_SECRET,
                grantType = "client_credentials"
            )
            cachedAccessToken = "Bearer ${response.accessToken}"
            tokenExpirationTime = currentTime + response.expiresIn * 1000
        }
        return cachedAccessToken ?: throw IllegalStateException("Access token is null")
    }

    override suspend fun insertMetadata(metadata: MusicMetadata) {
        roomDataSource.insertMetadata(metadata.toMusicMetadataEntity())
    }

    override suspend fun insertMetadataList(metadataList: List<MusicMetadata>) {
        roomDataSource.insertMetadataList(metadataList.toMusicMetadataEntityList())
    }

    override fun getAlbums(count: Int): Flow<List<String>> {
        return roomDataSource.getAlbums(count)
    }

    override fun getPagedAlbums(pageSize: Int): Flow<PagingData<String>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { roomDataSource.getPagedAlbums() }
        ).flow
    }

    override fun getArtists(count: Int): Flow<List<String>> {
        return roomDataSource.getArtists(count)
    }

    override fun getPagedArtists(pageSize: Int): Flow<PagingData<String>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { roomDataSource.getPagedArtists() }
        ).flow
    }

    override fun getArtistFromAlbum(album: String): String {
        return roomDataSource.getArtistFromAlbum(album)
    }

    override fun getGenres(count: Int): Flow<List<String>> {
        return roomDataSource.getGenres(count)
    }

    override fun getPagedGenres(pageSize: Int): Flow<PagingData<String>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { roomDataSource.getPagedGenres() }
        ).flow
    }

    override fun getYears(count: Int): Flow<List<String>> {
        return roomDataSource.getYears(count)
    }

    override fun getPagedYears(pageSize: Int): Flow<PagingData<String>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { roomDataSource.getPagedYears() }
        ).flow
    }

    override fun getMetadata(pageSize: Int): Flow<PagingData<MusicMetadata>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { roomDataSource.getPagedMetadata() }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                val albumArt = getAlbumArtByAlbum(entity.album)
                entity.toMusicMetadata(albumArt?.imageUrl)
            }
        }
    }

    override fun getMetadataByAlbum(album: String): Flow<MusicMetadata> {
        return roomDataSource.getMetadataByAlbum(album).map { entity ->
            val albumArt = getAlbumArtByAlbum(entity.album)
            entity.toMusicMetadata(albumArt?.imageUrl)
        }
    }

    override fun getMetadataByAlbum(album: String, pageSize: Int): Flow<PagingData<MusicMetadata>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { roomDataSource.getPagedMetadataByAlbum(album) }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                val albumArt = getAlbumArtByAlbum(entity.album)
                entity.toMusicMetadata(albumArt?.imageUrl)
            }
        }
    }

    override fun getMetadataByArtist(artist: String): Flow<MusicMetadata> {
        return roomDataSource.getMetadataByArtist(artist).map { entity ->
            val albumArt = getAlbumArtByAlbum(entity.album)
            entity.toMusicMetadata(albumArt?.imageUrl)
        }
    }

    override fun getMetadataByArtist(
        artist: String,
        pageSize: Int
    ): Flow<PagingData<MusicMetadata>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { roomDataSource.getPagedMetadataByArtist(artist) }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                val albumArt = getAlbumArtByAlbum(entity.album)
                entity.toMusicMetadata(albumArt?.imageUrl)
            }
        }
    }

    override fun getMetadataByGenre(genre: String): Flow<MusicMetadata> {
        return roomDataSource.getMetadataByGenre(genre).map { entity ->
            val albumArt = getAlbumArtByAlbum(entity.album)
            entity.toMusicMetadata(albumArt?.imageUrl)
        }
    }

    override fun getMetadataByGenre(genre: String, pageSize: Int): Flow<PagingData<MusicMetadata>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { roomDataSource.getPagedMetadataByGenre(genre) }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                val albumArt = getAlbumArtByAlbum(entity.album)
                entity.toMusicMetadata(albumArt?.imageUrl)
            }
        }
    }

    override fun getMetadataByYear(year: String): Flow<MusicMetadata> {
        return roomDataSource.getMetadataByYear(year).map { entity ->
            val albumArt = getAlbumArtByAlbum(entity.album)
            entity.toMusicMetadata(albumArt?.imageUrl)
        }
    }

    override fun getMetadataByYear(year: String, pageSize: Int): Flow<PagingData<MusicMetadata>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { roomDataSource.getPagedMetadataByYear(year) }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                val albumArt = getAlbumArtByAlbum(entity.album)
                entity.toMusicMetadata(albumArt?.imageUrl)
            }
        }
    }

    override suspend fun isExistMetadata(absolutePath: String): Boolean {
        return roomDataSource.isExistMetadata(absolutePath)
    }

    override suspend fun deleteAllMetadataList() {
        roomDataSource.deleteAllMetadataList()
    }

    override suspend fun deleteMetadata(absolutePath: String) {
        roomDataSource.deleteMetadata(absolutePath)
    }

    override suspend fun getMetadataCount(): Int {
        return roomDataSource.getMetadataCount()
    }

    override suspend fun getMetadataCountByAlbum(album: String): Int {
        return roomDataSource.getMetadataCountByAlbum(album)
    }

    override suspend fun getMetadataCountByAlbumOfArtist(artist: String): Int {
        return roomDataSource.getMetadataCountByAlbumOfArtist(artist)
    }

    override suspend fun getMetadataCountByArtist(artist: String): Int {
        return roomDataSource.getMetadataCountByArtist(artist)
    }

    override suspend fun getMetadataCountByGenre(genre: String): Int {
        return roomDataSource.getMetadataCountByGenre(genre)
    }

    override suspend fun getMetadataCountByYear(year: String): Int {
        return roomDataSource.getMetadataCountByYear(year)
    }

    override suspend fun insertAlbumArt(albumArt: AlbumArt) {
        return roomDataSource.insertAlbumArt(albumArt.toAlbumArtEntity())
    }

    override suspend fun getAlbumArtByAlbum(album: String): AlbumArt? {
        return roomDataSource.getAlbumArtByAlbum(album)?.toAlbumArt() ?: run {
            searchAlbum(album)?.let {
                val albumArt = AlbumArt(
                    album = album,
                    imageUrl = it.images.firstOrNull()?.url,
                    id = it.id
                )
                insertAlbumArt(albumArt)

                albumArt
            }
        }
    }

    override suspend fun getAlbumArtById(id: String): AlbumArt {
        return roomDataSource.getAlbumArtById(id)?.toAlbumArt() ?: run {
            getAlbumDetails(id).let {
                val albumArt = AlbumArt(
                    album = it.name,
                    imageUrl = it.images.firstOrNull()?.url,
                    id = it.id
                )
                insertAlbumArt(albumArt)

                albumArt
            }
        }
    }

    override suspend fun deleteAllAlbumArt() {
        return roomDataSource.deleteAllAlbumArt()
    }

    override suspend fun insertArtistInfo(artistInfo: ArtistInfo) {
        return roomDataSource.insertArtistInfo(artistInfo.toArtistInfoEntity())
    }

    override suspend fun getArtistInfoByArtist(artist: String): ArtistInfo? {
        return roomDataSource.getArtistInfoByArtist(artist)?.toArtistInfo() ?: run {
            searchArtist(artist)?.let {
                val artistInfo = ArtistInfo(
                    artist = artist,
                    imageUrl = it.images?.firstOrNull()?.url,
                    id = it.id
                )
                insertArtistInfo(artistInfo)

                artistInfo
            }
        }
    }

    override suspend fun getArtistInfoById(id: String): ArtistInfo {
        return roomDataSource.getArtistInfoById(id)?.toArtistInfo() ?: run {
            getArtistDetails(id).let {
                val artistInfo = ArtistInfo(
                    artist = it.name,
                    imageUrl = it.images?.firstOrNull()?.url,
                    id = it.id
                )
                insertArtistInfo(artistInfo)

                artistInfo
            }
        }
    }

    override suspend fun deleteAllArtistInfo() {
        return roomDataSource.deleteAllArtistInfo()
    }

    override fun getMusicMetadata(file: File): MusicMetadata? {
        return mediaDataSource.getMusicMetadata(file)
    }

    override suspend fun getMusicMetadataFlow(file: File): Flow<MusicMetadata?> {
        return mediaDataSource.getMusicMetadataFlow(file)
    }

    override suspend fun search(
        query: String,
        type: String,
        market: String,
        limit: Int,
        offset: Int
    ): Search {
        return spotifyDataSource.search(query, type, market, limit, offset, getAccessToken())
            .toSearch()
    }

    override suspend fun searchTrack(trackName: String, artistName: String): TrackDetails? {
        return spotifyDataSource.search(
            query = "$trackName artist:$artistName",
            type = "track",
            accessToken = getAccessToken()
        ).let { search ->
            search.tracks?.items?.firstOrNull()?.toTrackDetails()
        }
    }

    override suspend fun searchArtist(artistName: String): ArtistDetails? {
        return spotifyDataSource.search(
            query = artistName,
            type = "artist",
            limit = 1,
            accessToken = getAccessToken()
        ).let { search ->
            search.artists?.items?.firstOrNull()?.toArtistDetails()
        }
    }

    override suspend fun searchAlbum(albumName: String): AlbumDetails? {
        return spotifyDataSource.search(
            query = albumName,
            type = "album",
            accessToken = getAccessToken()
        ).let { search ->
            search.albums?.items?.firstOrNull()?.toAlbumDetails()
        }
    }

    override suspend fun getTrackDetails(trackId: String): TrackDetails {
        return spotifyDataSource.getTrackDetails(trackId, getAccessToken()).toTrackDetails()
    }

    override suspend fun getArtistDetails(artistId: String): ArtistDetails {
        return spotifyDataSource.getArtistDetails(artistId, getAccessToken()).toArtistDetails()
    }

    override suspend fun getAlbumDetails(albumId: String): AlbumDetails {
        return spotifyDataSource.getAlbumDetails(albumId, getAccessToken()).toAlbumDetails()
    }

    override suspend fun insertFavorite(favorite: Favorite) {
        roomDataSource.insertFavorite(favorite.toFavoriteEntity())
    }

    override fun isFavorite(name: String, type: String): Flow<Boolean> {
        return roomDataSource.isFavorite(name, type)
    }

    override fun getFavorites(count: Int): Flow<List<Favorite>> {
        return roomDataSource.getFavorites(count).map { it.toFavoriteList() }
    }

    override fun getPagedFavorites(pageSize: Int): Flow<PagingData<Favorite>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { roomDataSource.getPagedFavorites() }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                entity.toFavorite()
            }
        }
    }

    override fun getPagedFavoritesByType(type: String, pageSize: Int): Flow<PagingData<Favorite>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { roomDataSource.getPagedFavoritesByType(type) }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                entity.toFavorite()
            }
        }
    }

    override suspend fun deleteFavorite(name: String, type: String) {
        roomDataSource.deleteFavorite(name, type)
    }

    override suspend fun deleteAllFavorites() {
        roomDataSource.deleteAllFavorites()
    }
}