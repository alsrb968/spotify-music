package com.litbig.spotify.ui.models

import androidx.compose.ui.graphics.Color
import com.litbig.spotify.core.domain.model.remote.PlaylistDetails

data class PlaylistUiModel(
    val id: String,
    val imageUrl: String?,
    val name: String,
    val follower: Int,
    val description: String,
    val dominantColor: Color,
) {
    companion object {
        @JvmStatic
        fun from(playlist: PlaylistDetails): PlaylistUiModel {
            return PlaylistUiModel(
                id = playlist.id,
                imageUrl = playlist.images.firstOrNull()?.url,
                name = playlist.name,
                follower = playlist.followers?.total ?: 0,
                description = playlist.description,
                dominantColor = Color.Transparent,
            )
        }
    }
}
