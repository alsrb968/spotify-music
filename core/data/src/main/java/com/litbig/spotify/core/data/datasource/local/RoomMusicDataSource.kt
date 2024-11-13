package com.litbig.spotify.core.data.datasource.local

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.litbig.spotify.core.data.db.MusicMetadataDao
import com.litbig.spotify.core.data.model.local.MusicMetadataEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RoomMusicDataSource {
    suspend fun insertMetadata(metadata: MusicMetadataEntity)
    suspend fun insertMetadataList(metadataList: List<MusicMetadataEntity>)
    fun getPagedAlbums(pageSize: Int = 20): Flow<PagingData<String>>
    fun getPagedArtists(pageSize: Int = 20): Flow<PagingData<String>>
    fun getPagedGenres(pageSize: Int = 20): Flow<PagingData<String>>
    fun getPagedYears(pageSize: Int = 20): Flow<PagingData<String>>
    fun getPagedMetadata(pageSize: Int = 20): Flow<PagingData<MusicMetadataEntity>>
    fun getPagedMetadataByAlbum(
        album: String,
        pageSize: Int = 20
    ): Flow<PagingData<MusicMetadataEntity>>

    fun getPagedMetadataByArtist(
        artist: String,
        pageSize: Int = 20
    ): Flow<PagingData<MusicMetadataEntity>>

    fun getPagedMetadataByGenre(
        genre: String,
        pageSize: Int = 20
    ): Flow<PagingData<MusicMetadataEntity>>

    fun getPagedMetadataByYear(
        year: String,
        pageSize: Int = 20
    ): Flow<PagingData<MusicMetadataEntity>>

    suspend fun isExistMetadata(absolutePath: String): Boolean
    suspend fun deleteAllMetadataList()
    suspend fun deleteMetadata(absolutePath: String)
    suspend fun getMetadataCount(): Int
    suspend fun getMetadataCountByAlbum(album: String): Int
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

    override fun getPagedAlbums(pageSize: Int): Flow<PagingData<String>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { metadataDao.getPagedAlbums() }
        ).flow
    }

    override fun getPagedArtists(pageSize: Int): Flow<PagingData<String>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { metadataDao.getPagedArtists() }
        ).flow
    }

    override fun getPagedGenres(pageSize: Int): Flow<PagingData<String>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { metadataDao.getPagedGenres() }
        ).flow
    }

    override fun getPagedYears(pageSize: Int): Flow<PagingData<String>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { metadataDao.getPagedYears() }
        ).flow
    }

    override fun getPagedMetadata(pageSize: Int): Flow<PagingData<MusicMetadataEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { metadataDao.getPagedMetadata() }
        ).flow
    }

    override fun getPagedMetadataByAlbum(
        album: String,
        pageSize: Int
    ): Flow<PagingData<MusicMetadataEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { metadataDao.getPagedMetadataByAlbum(album) }
        ).flow
    }

    override fun getPagedMetadataByArtist(
        artist: String,
        pageSize: Int
    ): Flow<PagingData<MusicMetadataEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { metadataDao.getPagedMetadataByArtist(artist) }
        ).flow
    }

    override fun getPagedMetadataByGenre(
        genre: String,
        pageSize: Int
    ): Flow<PagingData<MusicMetadataEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { metadataDao.getPagedMetadataByGenre(genre) }
        ).flow
    }

    override fun getPagedMetadataByYear(
        year: String,
        pageSize: Int
    ): Flow<PagingData<MusicMetadataEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { metadataDao.getPagedMetadataByYear(year) }
        ).flow
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