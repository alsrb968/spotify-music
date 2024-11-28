package com.litbig.spotify.core.domain.repository

interface StorageRepository {
    suspend fun addStorageHash(path: String, hash: String)
    suspend fun getStorageHash(path: String): String?
    suspend fun removeStorageHash(path: String)
    suspend fun removeAllStorageHash()
}