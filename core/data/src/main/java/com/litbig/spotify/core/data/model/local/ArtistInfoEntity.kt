package com.litbig.spotify.core.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artist_info")
data class ArtistInfoEntity(
    @PrimaryKey val artist: String,
    val imageUrl: String?,
    val id: String
)
