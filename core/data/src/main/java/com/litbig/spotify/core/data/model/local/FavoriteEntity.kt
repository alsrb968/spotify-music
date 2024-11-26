package com.litbig.spotify.core.data.model.local

import androidx.room.Entity

@Entity(
    tableName = "favorite",
    primaryKeys = ["name", "type"]
)
data class FavoriteEntity(
    val name: String,
    val type: String,
    val imageUrl: String?,
)