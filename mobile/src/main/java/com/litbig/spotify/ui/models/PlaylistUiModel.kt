package com.litbig.spotify.ui.models

import com.litbig.spotify.core.domain.model.remote.PlaylistDetails

data class PlaylistUiModel(
    val id: String,
    val imageUrl: String?,
    val name: String,
    val description: String,
) {
    companion object {
        @JvmStatic
        fun from(playlist: PlaylistDetails): PlaylistUiModel {
            return PlaylistUiModel(
                id = playlist.id,
                imageUrl = playlist.images.firstOrNull()?.url,
                name = playlist.name,
                description = playlist.description,
            )
        }
    }
}
