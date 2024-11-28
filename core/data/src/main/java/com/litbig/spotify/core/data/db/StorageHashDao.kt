package com.litbig.spotify.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.litbig.spotify.core.data.model.local.StorageHashEntity

@Dao
interface StorageHashDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStorageHash(storageHash: StorageHashEntity)

    @Query(
        """
        SELECT hash
        FROM storage_hash
        WHERE path = :path
        """
    )
    suspend fun getHash(path: String): String?

    @Query(
        """
        DELETE FROM storage_hash
        WHERE path = :path
        """
    )
    suspend fun deleteStorageHash(path: String)

    @Query(
        """
        DELETE
        FROM storage_hash
        """
    )
    suspend fun deleteAllStorageHash()

}