package com.litbig.spotify.core.data.mapper.local

import com.litbig.spotify.core.data.model.local.FavoriteEntity
import com.litbig.spotify.core.domain.model.local.Favorite

fun FavoriteEntity.toFavorite(): Favorite {
    return Favorite(
        name = name,
        type = type,
        imageUrl = imageUrl,
    )
}

fun Favorite.toFavoriteEntity(): FavoriteEntity {
    return FavoriteEntity(
        name = name,
        type = type,
        imageUrl = imageUrl,
    )
}