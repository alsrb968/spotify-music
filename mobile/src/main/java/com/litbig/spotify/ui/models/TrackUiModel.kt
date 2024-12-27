package com.litbig.spotify.ui.models

import com.litbig.spotify.core.domain.model.remote.TrackDetails

data class TrackUiModel(
    override val id: String,
    override val imageUrl: String?,
    override val name: String,
    val artists: String,
    val duration: Long,
) : UiModel {
    companion object {
        @JvmStatic
        fun from(trackDetails: TrackDetails): TrackUiModel {
            return TrackUiModel(
                id = trackDetails.id,
                imageUrl = trackDetails.album?.images?.firstOrNull()?.url,
                name = trackDetails.name,
                artists = trackDetails.artists.joinToString { it.name },
                duration = trackDetails.durationMs.toLong(),
            )
        }
    }
}