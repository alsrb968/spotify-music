package com.litbig.spotify.core.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.litbig.spotify.core.data.model.local.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM favorite
            WHERE name = :name AND type = :type
        )
        """
    )
    fun isFavorite(name: String, type: String): Flow<Boolean>

    @Query(
        """
        SELECT *
        FROM favorite
        ORDER BY ROWID DESC
        """
    )
    fun getPagedFavorites(): PagingSource<Int, FavoriteEntity>

    @Query(
        """
        SELECT *
        FROM favorite
        WHERE type = :type
        ORDER By ROWID DESC
        """
    )
    fun getPagedFavoritesByType(type: String): PagingSource<Int, FavoriteEntity>

    @Query(
        """
        DELETE
        FROM favorite
        WHERE name = :name AND type = :type
        """
    )
    suspend fun deleteFavorite(name: String, type: String)

    @Query(
        """
        DELETE
        FROM favorite
        """
    )
    suspend fun deleteAllFavorites()
}