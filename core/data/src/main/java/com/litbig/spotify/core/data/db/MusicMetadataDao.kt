package com.litbig.spotify.core.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.litbig.spotify.core.data.model.local.MusicMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadata(metadata: MusicMetadataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadataList(metadataList: List<MusicMetadataEntity>)

    @Query(
        """
        SELECT DISTINCT album
        FROM music_metadata
        ORDER BY album
        LIMIT :count
        """
    )
    fun getAlbums(count: Int = 10): Flow<List<String>>

    @Query(
        """
        SELECT DISTINCT album 
        FROM music_metadata 
        ORDER BY album
        """
    )
    fun getPagedAlbums(): PagingSource<Int, String>

    @Query(
        """
        SELECT DISTINCT artist 
        FROM music_metadata 
        ORDER BY artist
        LIMIT :count
        """
    )
    fun getArtists(count: Int = 10): Flow<List<String>>

    @Query(
        """
        SELECT DISTINCT artist 
        FROM music_metadata 
        ORDER BY artist
        """
    )
    fun getPagedArtists(): PagingSource<Int, String>

    @Query(
        """
        SELECT artist 
        FROM music_metadata 
        WHERE album = :album LIMIT 1
        """
    )
    fun getArtistFromAlbum(album: String): String

    @Query(
        """
        SELECT DISTINCT genre 
        FROM music_metadata 
        ORDER BY genre
        LIMIT :count
        """
    )
    fun getGenres(count: Int = 10): Flow<List<String>>

    @Query(
        """
        SELECT DISTINCT genre 
        FROM music_metadata 
        ORDER BY genre
        """
    )
    fun getPagedGenres(): PagingSource<Int, String>

    @Query(
        """
        SELECT DISTINCT SUBSTR(year, 1, 4) AS year_only 
        FROM music_metadata 
        ORDER BY year_only
        LIMIT :count
        """
    )
    fun getYears(count: Int = 10): Flow<List<String>>

    @Query(
        """
        SELECT DISTINCT SUBSTR(year, 1, 4) AS year_only 
        FROM music_metadata 
        ORDER BY year_only
        """
    )
    fun getPagedYears(): PagingSource<Int, String>

    @Query(
        """
        SELECT * 
        FROM music_metadata
        """
    )
    fun getPagedMetadata(): PagingSource<Int, MusicMetadataEntity>

    @Query(
        """
        SELECT * 
        FROM music_metadata 
        WHERE album = :album LIMIT 1
        """
    )
    fun getMetadataByAlbum(album: String): Flow<MusicMetadataEntity>

    @Query(
        """
        SELECT * 
        FROM music_metadata 
        WHERE album = :album
        """
    )
    fun getPagedMetadataByAlbum(album: String): PagingSource<Int, MusicMetadataEntity>

    @Query(
        """
        SELECT * 
        FROM music_metadata 
        WHERE LOWER(REPLACE(artist, ' ', '')) LIKE '%' || LOWER(REPLACE(:artist, ' ', '')) || '%' LIMIT 1
        """
    )
    fun getMetadataByArtist(artist: String): Flow<MusicMetadataEntity>

    @Query(
        """
        SELECT * 
        FROM music_metadata 
        WHERE LOWER(REPLACE(artist, ' ', '')) LIKE '%' || LOWER(REPLACE(:artist, ' ', '')) || '%'
        """
    )
    fun getPagedMetadataByArtist(artist: String): PagingSource<Int, MusicMetadataEntity>

    @Query(
        """
        SELECT * 
        FROM music_metadata 
        WHERE genre = :genre LIMIT 1
        """
    )
    fun getMetadataByGenre(genre: String): Flow<MusicMetadataEntity>

    @Query(
        """
        SELECT * 
        FROM music_metadata 
        WHERE genre = :genre
        """
    )
    fun getPagedMetadataByGenre(genre: String): PagingSource<Int, MusicMetadataEntity>

    @Query(
        """
        SELECT * 
        FROM music_metadata 
        WHERE year LIKE :year || '-%' LIMIT 1
        """
    )
    fun getMetadataByYear(year: String): Flow<MusicMetadataEntity>

    @Query(
        """
        SELECT * 
        FROM music_metadata 
        WHERE year LIKE :year || '-%'
        """
    )
    fun getPagedMetadataByYear(year: String): PagingSource<Int, MusicMetadataEntity>

    @Query(
        """
        SELECT EXISTS(
            SELECT * 
            FROM music_metadata 
            WHERE absolutePath = :absolutePath
        )
        """
    )
    suspend fun isExistMetadata(absolutePath: String): Boolean

    @Query(
        """
        DELETE 
        FROM music_metadata
        """
    )
    suspend fun deleteAllMetadataList()

    @Query(
        """
        DELETE 
        FROM music_metadata 
        WHERE absolutePath = :absolutePath
        """
    )
    suspend fun deleteMetadata(absolutePath: String)

    @Query(
        """
        SELECT COUNT(*) 
        FROM music_metadata
        """
    )
    suspend fun getMetadataCount(): Int

    @Query(
        """
        SELECT COUNT(*) 
        FROM music_metadata 
        WHERE album = :album
        """
    )
    suspend fun getMetadataCountByAlbum(album: String): Int

    @Query(
        """
        SELECT COUNT(DISTINCT album) 
        FROM music_metadata 
        WHERE LOWER(REPLACE(artist, ' ', '')) LIKE '%' || LOWER(REPLACE(:artist, ' ', '')) || '%'
        """
    )
    suspend fun getMetadataCountByAlbumOfArtist(artist: String): Int

    @Query(
        """
        SELECT COUNT(*) 
        FROM music_metadata 
        WHERE LOWER(REPLACE(artist, ' ', '')) LIKE '%' || LOWER(REPLACE(:artist, ' ', '')) || '%'
        """
    )
    suspend fun getMetadataCountByArtist(artist: String): Int

    @Query(
        """
        SELECT COUNT(*) 
        FROM music_metadata 
        WHERE genre = :genre
        """
    )
    suspend fun getMetadataCountByGenre(genre: String): Int

    @Query(
        """
        SELECT COUNT(*) 
        FROM music_metadata 
        WHERE year LIKE :year || '-%'
        """
    )
    suspend fun getMetadataCountByYear(year: String): Int
}