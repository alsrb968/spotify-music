package com.litbig.spotify.core.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "storage_hash")
data class StorageHashEntity(
    @PrimaryKey val path: String,
    val hash: String
)
