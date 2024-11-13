package com.litbig.spotify.core.data.repository

import android.graphics.Bitmap
import androidx.paging.PagingData
import androidx.paging.map
import com.litbig.spotify.core.data.datasource.local.MediaRetrieverDataSource
import com.litbig.spotify.core.data.datasource.local.RoomMusicDataSource
import com.litbig.spotify.core.data.mapper.local.MusicMetadataMapper.toMusicMetadata
import com.litbig.spotify.core.data.mapper.local.MusicMetadataMapper.toMusicMetadataEntity
import com.litbig.spotify.core.data.mapper.local.MusicMetadataMapper.toMusicMetadataEntityList
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val roomDataSource: RoomMusicDataSource,
    private val mediaDataSource: MediaRetrieverDataSource,
) : MusicRepository {
    override suspend fun insertMetadata(metadata: MusicMetadata) {
        roomDataSource.insertMetadata(metadata.toMusicMetadataEntity())
    }

    override suspend fun insertMetadataList(metadataList: List<MusicMetadata>) {
        roomDataSource.insertMetadataList(metadataList.toMusicMetadataEntityList())
    }

    override fun getAlbums(pageSize: Int): Flow<PagingData<String>> {
        return roomDataSource.getPagedAlbums(pageSize)
    }

    override fun getArtists(pageSize: Int): Flow<PagingData<String>> {
        return roomDataSource.getPagedArtists(pageSize)
    }

    override fun getGenres(pageSize: Int): Flow<PagingData<String>> {
        return roomDataSource.getPagedGenres(pageSize)
    }

    override fun getYears(pageSize: Int): Flow<PagingData<String>> {
        return roomDataSource.getPagedYears(pageSize)
    }

    override fun getMetadata(pageSize: Int): Flow<PagingData<MusicMetadata>> {
        return roomDataSource.getPagedMetadata(pageSize).map { pagingData ->
            pagingData.map { entity ->
                val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath)).first()
                entity.toMusicMetadata(albumArt)
            }
        }
    }

    override fun getMetadataByAlbum(album: String, pageSize: Int): Flow<PagingData<MusicMetadata>> {
        return roomDataSource.getPagedMetadataByAlbum(album, pageSize).map { pagingData ->
            pagingData.map { entity ->
                val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath)).first()
                entity.toMusicMetadata(albumArt)
            }
        }
    }

    override fun getMetadataByArtist(
        artist: String,
        pageSize: Int
    ): Flow<PagingData<MusicMetadata>> {
        return roomDataSource.getPagedMetadataByArtist(artist, pageSize).map { pagingData ->
            pagingData.map { entity ->
                val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath)).first()
                entity.toMusicMetadata(albumArt)
            }
        }
    }

    override fun getMetadataByGenre(genre: String, pageSize: Int): Flow<PagingData<MusicMetadata>> {
        return roomDataSource.getPagedMetadataByGenre(genre, pageSize).map { pagingData ->
            pagingData.map { entity ->
                val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath)).first()
                entity.toMusicMetadata(albumArt)
            }
        }
    }

    override fun getMetadataByYear(year: String, pageSize: Int): Flow<PagingData<MusicMetadata>> {
        return roomDataSource.getPagedMetadataByYear(year, pageSize).map { pagingData ->
            pagingData.map { entity ->
                val albumArt = mediaDataSource.getAlbumArt(File(entity.absolutePath)).first()
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

    override suspend fun getAlbumArt(file: File): Flow<Bitmap?> {
        return mediaDataSource.getAlbumArt(file)
    }

    override suspend fun getAlbumArtList(files: List<File>): Flow<List<Bitmap?>> {
        return mediaDataSource.getAlbumArtList(files)
    }

    override fun getMusicMetadataWithoutFlow(file: File): MusicMetadata? {
        return mediaDataSource.getMusicMetadataWithoutFlow(file)
    }

    override suspend fun getMusicMetadata(file: File): Flow<MusicMetadata?> {
        return mediaDataSource.getMusicMetadata(file)
    }

    override suspend fun getMusicMetadataList(files: List<File>): Flow<List<MusicMetadata?>> {
        return mediaDataSource.getMusicMetadataList(files)
    }
}