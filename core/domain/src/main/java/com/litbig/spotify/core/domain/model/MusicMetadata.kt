package com.litbig.spotify.core.domain.model

import android.graphics.Bitmap
import kotlin.time.Duration

data class MusicMetadata(
    val absolutePath: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumArt: Bitmap?,
    val genre: String,
    val duration: Duration,
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
