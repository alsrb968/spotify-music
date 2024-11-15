package com.litbig.spotify.core.data.repository

import android.graphics.Bitmap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.litbig.spotify.core.data.BuildConfig
import com.litbig.spotify.core.data.datasource.local.MediaRetrieverDataSource
import com.litbig.spotify.core.data.datasource.local.RoomMusicDataSource
import com.litbig.spotify.core.data.datasource.remote.SpotifyDataSource
import com.litbig.spotify.core.data.mapper.local.toMusicMetadata
import com.litbig.spotify.core.data.mapper.local.toMusicMetadataEntity
import com.litbig.spotify.core.data.mapper.local.toMusicMetadataEntityList
import com.litbig.spotify.core.data.mapper.remote.toAlbumDetails
import com.litbig.spotify.core.data.mapper.remote.toArtistDetails
import com.litbig.spotify.core.data.mapper.remote.toSearch
import com.litbig.spotify.core.data.mapper.remote.toTrackDetails
import com.litbig.spotify.core.domain.model.*
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

    override fun getAlbums(): Flow<List<String>> {
        return roomDataSource.getAlbums()
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

    override fun getArtists(): Flow<List<String>> {
        return roomDataSource.getArtists()
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

    override fun getGenres(): Flow<List<String>> {
        return roomDataSource.getGenres()
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

    override fun getYears(): Flow<List<String>> {
        return roomDataSource.getYears()
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
                val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath))
                entity.toMusicMetadata(albumArt)
            }
        }
    }

    override fun getMetadataByAlbum(album: String): Flow<MusicMetadata> {
        return roomDataSource.getMetadataByAlbum(album).map { entity ->
            val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath))
            entity.toMusicMetadata(albumArt)
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
                val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath))
                entity.toMusicMetadata(albumArt)
            }
        }
    }

    override fun getMetadataByArtist(artist: String): Flow<MusicMetadata> {
        return roomDataSource.getMetadataByArtist(artist).map { entity ->
            val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath))
            entity.toMusicMetadata(albumArt)
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
                val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath))
                entity.toMusicMetadata(albumArt)
            }
        }
    }

    override fun getMetadataByGenre(genre: String): Flow<MusicMetadata> {
        return roomDataSource.getMetadataByGenre(genre).map { entity ->
            val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath))
            entity.toMusicMetadata(albumArt)
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
                val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath))
                entity.toMusicMetadata(albumArt)
            }
        }
    }

    override fun getMetadataByYear(year: String): Flow<MusicMetadata> {
        return roomDataSource.getMetadataByYear(year).map { entity ->
            val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath))
            entity.toMusicMetadata(albumArt)
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
                val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath))
                entity.toMusicMetadata(albumArt)
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

    override suspend fun getMetadataCountByArtist(artist: String): Int {
        return roomDataSource.getMetadataCountByArtist(artist)
    }

    override suspend fun getMetadataCountByGenre(genre: String): Int {
        return roomDataSource.getMetadataCountByGenre(genre)
    }

    override suspend fun getMetadataCountByYear(year: String): Int {
        return roomDataSource.getMetadataCountByYear(year)
    }

    override fun getAlbumArt(file: File): Bitmap? {
        return mediaDataSource.getAlbumArt(file)
    }

    override suspend fun getAlbumArtFlow(file: File): Flow<Bitmap?> {
        return mediaDataSource.getAlbumArtFlow(file)
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

    override suspend fun getTrackDetails(trackId: String): TrackDetails {
        return spotifyDataSource.getTrackDetails(trackId, getAccessToken()).toTrackDetails()
    }

    override suspend fun getArtistDetails(artistId: String): ArtistDetails {
        return spotifyDataSource.getArtistDetails(artistId, getAccessToken()).toArtistDetails()
    }

    override suspend fun getAlbumDetails(albumId: String): AlbumDetails {
        return spotifyDataSource.getAlbumDetails(albumId, getAccessToken()).toAlbumDetails()
    }
}