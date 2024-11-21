package com.litbig.spotify.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.litbig.spotify.core.data.model.local.AlbumArtEntity

@Dao
interface AlbumArtDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbumArt(albumArt: AlbumArtEntity)

    @Query(
        """
        SELECT *
        FROM album_art
        WHERE album = :album
        """
    )
    suspend fun getAlbumArtByAlbum(album: String): AlbumArtEntity?

    @Query(
        """
        SELECT *
        FROM album_art
        WHERE id = :id
        """
    )
    suspend fun getAlbumArtById(id: String): AlbumArtEntity?

    @Query(
        """
        DELETE
        FROM album_art
        """
    )
    suspend fun deleteAllAlbumArt()
}