package com.litbig.spotify.ui.models

import androidx.compose.ui.graphics.Color
import com.litbig.spotify.core.domain.model.remote.AlbumDetails
import java.text.SimpleDateFormat
import java.util.Locale

data class AlbumUiModel(
    val id: String,
    val imageUrl: String?,
    val albumType: String,
    val name: String,
    val artists: String,
    val totalTime: Long,
    val releaseDate: Long,
    val copyright: String?,
    val dominantColor: Color,
) {
    companion object {
        @JvmStatic
        fun from(albumDetails: AlbumDetails): AlbumUiModel {
            return AlbumUiModel(
                id = albumDetails.id,
                imageUrl = albumDetails.images.firstOrNull()?.url,
                albumType = albumDetails.albumType,
                name = albumDetails.name,
                artists = albumDetails.artists.joinToString { it.name },
                totalTime = albumDetails.tracks?.items?.sumOf { it.durationMs }?.toLong() ?: 0L,
                releaseDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    .parse(albumDetails.releaseDate)?.time ?: 0L,
                copyright = albumDetails.copyrights?.map { it.text }?.joinToString("\n") { it },
                dominantColor = Color.Transparent,
            )
        }
    }
}