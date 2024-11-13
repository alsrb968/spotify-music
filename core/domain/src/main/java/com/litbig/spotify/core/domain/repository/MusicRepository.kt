package com.litbig.spotify.core.domain.repository

import android.graphics.Bitmap
import androidx.paging.PagingData
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import kotlinx.coroutines.flow.Flow
import java.io.File

interface MusicRepository {
    suspend fun insertMetadata(metadata: MusicMetadata)
    suspend fun insertMetadataList(metadataList: List<MusicMetadata>)
    fun getAlbums(pageSize: Int = 20): Flow<PagingData<String>>
    fun getArtists(pageSize: Int = 20): Flow<PagingData<String>>
    fun getGenres(pageSize: Int = 20): Flow<PagingData<String>>
    fun getYears(pageSize: Int = 20): Flow<PagingData<String>>
    fun getMetadata(pageSize: Int = 20): Flow<PagingData<MusicMetadata>>
    fun getMetadataByAlbum(
        album: String,
        pageSize: Int = 20
    ): Flow<PagingData<MusicMetadata>>

    fun getMetadataByArtist(
        artist: String,
        pageSize: Int = 20
    ): Flow<PagingData<MusicMetadata>>

    fun getMetadataByGenre(
        genre: String,
        pageSize: Int = 20
    ): Flow<PagingData<MusicMetadata>>

    fun getMetadataByYear(
        year: String,
        pageSize: Int = 20
    ): Flow<PagingData<MusicMetadata>>

    suspend fun isExistMetadata(absolutePath: String): Boolean
    suspend fun deleteAllMetadataList()
    suspend fun deleteMetadata(absolutePath: String)
    suspend fun getMetadataCount(): Int
    suspend fun getMetadataCountByAlbum(album: String): Int
    suspend fun getMetadataCountByArtist(artist: String): Int
    suspend fun getMetadataCountByGenre(genre: String): Int
    suspend fun getMetadataCountByYear(year: String): Int

    suspend fun getAlbumArt(file: File): Flow<Bitmap?>
    suspend fun getAlbumArtList(files: List<File>): Flow<List<Bitmap?>>
    fun getMusicMetadataWithoutFlow(file: File): MusicMetadata?
    suspend fun getMusicMetadata(file: File): Flow<MusicMetadata?>
    suspend fun getMusicMetadataList(files: List<File>): Flow<List<MusicMetadata?>>
}