package com.litbig.spotify.core.data.datasource.local

import androidx.paging.PagingSource
import com.litbig.spotify.core.data.db.MusicMetadataDao
import com.litbig.spotify.core.data.model.local.MusicMetadataEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RoomMusicDataSource {
    suspend fun insertMetadata(metadata: MusicMetadataEntity)
    suspend fun insertMetadataList(metadataList: List<MusicMetadataEntity>)

    fun getAlbums(): Flow<List<String>>
    fun getPagedAlbums(): PagingSource<Int, String>

    fun getArtists(): Flow<List<String>>
    fun getPagedArtists(): PagingSource<Int, String>
    fun getArtistFromAlbum(album: String): String

    fun getGenres(): Flow<List<String>>
    fun getPagedGenres(): PagingSource<Int, String>

    fun getYears(): Flow<List<String>>
    fun getPagedYears(): PagingSource<Int, String>

    fun getPagedMetadata(): PagingSource<Int, MusicMetadataEntity>

    fun getMetadataByAlbum(album: String): Flow<MusicMetadataEntity>
    fun getPagedMetadataByAlbum(album: String): PagingSource<Int, MusicMetadataEntity>

    fun getMetadataByArtist(artist: String): Flow<MusicMetadataEntity>
    fun getPagedMetadataByArtist(artist: String): PagingSource<Int, MusicMetadataEntity>

    fun getMetadataByGenre(genre: String): Flow<MusicMetadataEntity>
    fun getPagedMetadataByGenre(genre: String): PagingSource<Int, MusicMetadataEntity>

    fun getMetadataByYear(year: String): Flow<MusicMetadataEntity>
    fun getPagedMetadataByYear(year: String): PagingSource<Int, MusicMetadataEntity>

    suspend fun isExistMetadata(absolutePath: String): Boolean
    suspend fun deleteAllMetadataList()
    suspend fun deleteMetadata(absolutePath: String)
    suspend fun getMetadataCount(): Int
    suspend fun getMetadataCountByAlbum(album: String): Int
    suspend fun getMetadataCountByAlbumOfArtist(artist: String): Int
    suspend fun getMetadataCountByArtist(artist: String): Int
    suspend fun getMetadataCountByGenre(genre: String): Int
    suspend fun getMetadataCountByYear(year: String): Int
}

class RoomMusicDataSourceImpl @Inject constructor(
    private val metadataDao: MusicMetadataDao
) : RoomMusicDataSource {
    override suspend fun insertMetadata(metadata: MusicMetadataEntity) {
        metadataDao.insertMetadata(metadata)
    }

    override suspend fun insertMetadataList(metadataList: List<MusicMetadataEntity>) {
        metadataDao.insertMetadataList(metadataList)
    }

    override fun getAlbums(): Flow<List<String>> {
        return metadataDao.getAlbums()
    }

    override fun getPagedAlbums(): PagingSource<Int, String> {
        return metadataDao.getPagedAlbums()
    }

    override fun getArtists(): Flow<List<String>> {
        return metadataDao.getArtists()
    }

    override fun getPagedArtists(): PagingSource<Int, String> {
        return metadataDao.getPagedArtists()
    }

    override fun getArtistFromAlbum(album: String): String {
        return metadataDao.getArtistFromAlbum(album)
    }

    override fun getGenres(): Flow<List<String>> {
        return metadataDao.getGenres()
    }

    override fun getPagedGenres(): PagingSource<Int, String> {
        return metadataDao.getPagedGenres()
    }

    override fun getYears(): Flow<List<String>> {
        return metadataDao.getYears()
    }

    override fun getPagedYears(): PagingSource<Int, String> {
        return metadataDao.getPagedYears()
    }

    override fun getPagedMetadata(): PagingSource<Int, MusicMetadataEntity> {
        return metadataDao.getPagedMetadata()
    }

    override fun getMetadataByAlbum(album: String): Flow<MusicMetadataEntity> {
        return metadataDao.getMetadataByAlbum(album)
    }

    override fun getPagedMetadataByAlbum(album: String): PagingSource<Int, MusicMetadataEntity> {
        return metadataDao.getPagedMetadataByAlbum(album)
    }

    override fun getMetadataByArtist(artist: String): Flow<MusicMetadataEntity> {
        return metadataDao.getMetadataByArtist(artist)
    }

    override fun getPagedMetadataByArtist(artist: String): PagingSource<Int, MusicMetadataEntity> {
        return metadataDao.getPagedMetadataByArtist(artist)
    }

    override fun getMetadataByGenre(genre: String): Flow<MusicMetadataEntity> {
        return metadataDao.getMetadataByGenre(genre)
    }

    override fun getPagedMetadataByGenre(genre: String): PagingSource<Int, MusicMetadataEntity> {
        return metadataDao.getPagedMetadataByGenre(genre)
    }

    override fun getMetadataByYear(year: String): Flow<MusicMetadataEntity> {
        return metadataDao.getMetadataByYear(year)
    }

    override fun getPagedMetadataByYear(year: String): PagingSource<Int, MusicMetadataEntity> {
        return metadataDao.getPagedMetadataByYear(year)
    }

    override suspend fun isExistMetadata(absolutePath: String): Boolean {
        return metadataDao.isExistMetadata(absolutePath)
    }

    override suspend fun deleteAllMetadataList() {
        metadataDao.deleteAllMetadataList()
    }

    override suspend fun deleteMetadata(absolutePath: String) {
        metadataDao.deleteMetadata(absolutePath)
    }

    override suspend fun getMetadataCount(): Int {
        return metadataDao.getMetadataCount()
    }

    override suspend fun getMetadataCountByAlbum(album: String): Int {
        return metadataDao.getMetadataCountByAlbum(album)
    }

    override suspend fun getMetadataCountByAlbumOfArtist(artist: String): Int {
        return metadataDao.getMetadataCountByAlbumOfArtist(artist)
    }

    override suspend fun getMetadataCountByArtist(artist: String): Int {
        return metadataDao.getMetadataCountByArtist(artist)
    }

    override suspend fun getMetadataCountByGenre(genre: String): Int {
        return metadataDao.getMetadataCountByGenre(genre)
    }

    override suspend fun getMetadataCountByYear(year: String): Int {
        return metadataDao.getMetadataCountByYear(year)
    }
}