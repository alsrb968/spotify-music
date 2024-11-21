package com.litbig.spotify.core.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "album_art")
data class AlbumArtEntity(
    @PrimaryKey val album: String,
    val imageUrl: String?,
    val id: String
)
