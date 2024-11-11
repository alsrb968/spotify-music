package com.litbig.spotify.util

import androidx.compose.ui.graphics.ImageBitmap
import com.litbig.spotify.util.ConvertExtensions.toHumanReadableDuration

data class MusicMetadata(
    val absolutePath: String,
    val title: String,
    val artist: String,
    val album: String,
    val genre: String,
    val albumArt: ImageBitmap?,
    val duration: Long, // in milliseconds
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
) {
    val formattedDuration: String
        get() = duration.toHumanReadableDuration()
}
