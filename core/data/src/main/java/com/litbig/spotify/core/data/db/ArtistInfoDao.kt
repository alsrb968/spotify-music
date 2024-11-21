package com.litbig.spotify.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.litbig.spotify.core.data.model.local.ArtistInfoEntity

@Dao
interface ArtistInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtistInfo(artistInfo: ArtistInfoEntity)

    @Query(
        """
        SELECT *
        FROM artist_info
        WHERE artist = :artist
        """
    )
    suspend fun getArtistInfoByArtist(artist: String): ArtistInfoEntity?

    @Query(
        """
        SELECT *
        FROM artist_info
        WHERE id = :id
        """
    )
    suspend fun getArtistInfoById(id: String): ArtistInfoEntity?

    @Query(
        """
        DELETE
        FROM artist_info
        """
    )
    suspend fun deleteAllArtistInfo()
}