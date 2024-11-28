package com.litbig.spotify.core.data.mapper.local

import com.litbig.spotify.core.data.model.local.AlbumArtEntity
import com.litbig.spotify.core.domain.model.local.AlbumArt

fun AlbumArtEntity.toAlbumArt(): AlbumArt {
    return AlbumArt(
        album = album,
        imageUrl = imageUrl,
        id = id
    )
}

fun AlbumArt.toAlbumArtEntity(): AlbumArtEntity {
    return AlbumArtEntity(
        album = album,
        imageUrl = imageUrl,
        id = id
    )
}