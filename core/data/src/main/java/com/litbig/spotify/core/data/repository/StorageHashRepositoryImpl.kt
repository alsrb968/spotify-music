package com.litbig.spotify.core.data.repository

import com.litbig.spotify.core.data.datasource.local.RoomMusicDataSource
import com.litbig.spotify.core.data.mapper.local.toStorageHashEntity
import com.litbig.spotify.core.domain.model.local.StorageHash
import com.litbig.spotify.core.domain.repository.StorageRepository
import javax.inject.Inject

class StorageHashRepositoryImpl @Inject constructor(
    private val roomDataSource: RoomMusicDataSource
) : StorageRepository {
    override suspend fun addStorageHash(path: String, hash: String) {
        roomDataSource.insertStorageHash(StorageHash(path, hash).toStorageHashEntity())
    }

    override suspend fun getStorageHash(path: String): String? {
        return roomDataSource.getHash(path)
    }

    override suspend fun removeStorageHash(path: String) {
        roomDataSource.deleteStorageHash(path)
    }

    override suspend fun removeAllStorageHash() {
        roomDataSource.deleteAllStorageHash()
    }
}