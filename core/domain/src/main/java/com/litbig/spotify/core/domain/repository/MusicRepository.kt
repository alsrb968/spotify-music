package com.litbig.spotify.core.domain.repository

import androidx.paging.PagingData
import com.litbig.spotify.core.domain.model.local.AlbumArt
import com.litbig.spotify.core.domain.model.local.ArtistInfo
import com.litbig.spotify.core.domain.model.local.Favorite
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.core.domain.model.remote.*
import kotlinx.coroutines.flow.Flow
import java.io.File

interface MusicRepository {
    suspend fun insertMetadata(metadata: MusicMetadata)
    suspend fun insertMetadataList(metadataList: List<MusicMetadata>)
    fun getAlbums(count: Int = 10): Flow<List<String>>
    fun getPagedAlbums(pageSize: Int = 20): Flow<PagingData<String>>
    fun getArtists(count: Int = 10): Flow<List<String>>
    fun getPagedArtists(pageSize: Int = 20): Flow<PagingData<String>>
    fun getArtistFromAlbum(album: String): String
    fun getGenres(count: Int = 10): Flow<List<String>>
    fun getPagedGenres(pageSize: Int = 20): Flow<PagingData<String>>
    fun getYears(count: Int = 10): Flow<List<String>>
    fun getPagedYears(pageSize: Int = 20): Flow<PagingData<String>>
    fun getMetadata(pageSize: Int = 20): Flow<PagingData<MusicMetadata>>
    fun getMetadataByAbsolutePath(absolutePath: String): Flow<MusicMetadata>
    fun getMetadataByAlbum(album: String): Flow<MusicMetadata>
    fun getMetadataByAlbum(album: String, pageSize: Int = 20): Flow<PagingData<MusicMetadata>>
    fun getMetadataByArtist(artist: String): Flow<MusicMetadata>
    fun getMetadataByArtist(artist: String, pageSize: Int = 20): Flow<PagingData<MusicMetadata>>
    fun getMetadataByGenre(genre: String): Flow<MusicMetadata>
    fun getMetadataByGenre(genre: String, pageSize: Int = 20): Flow<PagingData<MusicMetadata>>
    fun getMetadataByYear(year: String): Flow<MusicMetadata>
    fun getMetadataByYear(year: String, pageSize: Int = 20): Flow<PagingData<MusicMetadata>>
    suspend fun isExistMetadata(absolutePath: String): Boolean
    suspend fun deleteAllMetadataList()
    suspend fun deleteMetadata(absolutePath: String)
    suspend fun getMetadataCount(): Int
    suspend fun getMetadataCountByAlbum(album: String): Int
    suspend fun getMetadataCountByAlbumOfArtist(artist: String): Int
    suspend fun getMetadataCountByArtist(artist: String): Int
    suspend fun getMetadataCountByGenre(genre: String): Int
    suspend fun getMetadataCountByYear(year: String): Int

    suspend fun insertAlbumArt(albumArt: AlbumArt)
    suspend fun getAlbumArtByAlbum(album: String): AlbumArt?
    suspend fun getAlbumArtById(id: String): AlbumArt
    suspend fun deleteAllAlbumArt()

    suspend fun insertArtistInfo(artistInfo: ArtistInfo)
    suspend fun getArtistInfoByArtist(artist: String): ArtistInfo?
    suspend fun getArtistInfoById(id: String): ArtistInfo
    suspend fun deleteAllArtistInfo()

    fun getMusicMetadata(file: File): MusicMetadata?
    suspend fun getMusicMetadataFlow(file: File): Flow<MusicMetadata?>

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
    suspend fun getTrackDetails(trackId: String): TrackDetails
    suspend fun getSeveralTrackDetails(trackIds: String): List<TrackDetails>
    suspend fun getArtistDetails(artistId: String): ArtistDetails
    suspend fun getSeveralArtistDetails(artistIds: String): List<ArtistDetails>
    suspend fun getAlbumsOfArtist(artistId: String, limit: Int = 10, offset: Int = 0): Albums
    suspend fun getTopTracksOfArtist(artistId: String): List<TrackDetails>
    suspend fun getAlbumDetails(albumId: String): AlbumDetails
    suspend fun getSeveralAlbumDetails(albumIds: String): List<AlbumDetails>
    suspend fun getNewAlbumReleases(limit: Int = 10, offset: Int = 0): Albums?

    suspend fun insertFavorite(favorite: Favorite)
    fun isFavorite(name: String, type: String): Flow<Boolean>
    fun getFavorites(count: Int = 10): Flow<List<Favorite>>
    fun getPagedFavorites(pageSize: Int = 20): Flow<PagingData<Favorite>>
    fun getPagedFavoritesByType(type: String, pageSize: Int = 20): Flow<PagingData<Favorite>>
    suspend fun deleteFavorite(name: String, type: String)
    suspend fun deleteAllFavorites()
}