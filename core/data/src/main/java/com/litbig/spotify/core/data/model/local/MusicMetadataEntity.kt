package com.litbig.spotify.core.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_metadata")
data class MusicMetadataEntity(
    @PrimaryKey val absolutePath: String,
    val title: String,
    val artist: String,
    val album: String,
    val genre: String,
    val duration: Long,
    val year: String,
    val albumArtist: String,
    val composer: String,
    val writer: String,
    val cdTrackNumber: String,
    val discNumber: String,
    val date: String,
    val mimeType: String,
    val compilation: String,
    val hasAudio: Boolean,
    val bitrate: String,
    val numTracks: String,
)
