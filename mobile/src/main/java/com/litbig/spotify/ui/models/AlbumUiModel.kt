package com.litbig.spotify.ui.models

import androidx.compose.ui.graphics.Color
import com.litbig.spotify.core.domain.model.remote.AlbumDetails

data class AlbumUiModel(
    val id: String,
    val imageUrl: String?,
    val name: String,
    val artists: String,
    val totalTime: Long,
    val dominantColor: Color,
) {
    companion object {
        @JvmStatic
        fun from(albumDetails: AlbumDetails): AlbumUiModel {
            return AlbumUiModel(
                id = albumDetails.id,
                imageUrl = albumDetails.images.firstOrNull()?.url,
                name = albumDetails.name,
                artists = albumDetails.artists.joinToString { it.name },
                totalTime = albumDetails.tracks?.items?.sumOf { it.durationMs }?.toLong() ?: 0L,
                dominantColor = Color.Transparent,
            )
        }
    }
}