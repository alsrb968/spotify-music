package com.litbig.spotify.core.data.mapper.local

import com.litbig.spotify.core.data.model.local.StorageHashEntity
import com.litbig.spotify.core.domain.model.local.StorageHash

fun StorageHashEntity.toStorageHash(): StorageHash {
    return StorageHash(
        path = path,
        hash = hash
    )
}

fun StorageHash.toStorageHashEntity(): StorageHashEntity {
    return StorageHashEntity(
        path = path,
        hash = hash
    )
}