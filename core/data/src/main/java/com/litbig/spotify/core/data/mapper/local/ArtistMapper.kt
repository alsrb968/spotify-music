package com.litbig.spotify.core.data.mapper.local

import com.litbig.spotify.core.data.model.local.ArtistInfoEntity
import com.litbig.spotify.core.domain.model.local.ArtistInfo

fun ArtistInfoEntity.toArtistInfo(): ArtistInfo {
    return ArtistInfo(
        artist = artist,
        imageUrl = imageUrl,
        id = id
    )
}

fun ArtistInfo.toArtistInfoEntity(): ArtistInfoEntity {
    return ArtistInfoEntity(
        artist = artist,
        imageUrl = imageUrl,
        id = id
    )
}