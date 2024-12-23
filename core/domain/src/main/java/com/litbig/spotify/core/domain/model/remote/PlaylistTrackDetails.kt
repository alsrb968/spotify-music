package com.litbig.spotify.core.domain.model.remote

data class PlaylistTrackDetails(
    val addedAt: String,
    val addedBy: AddedBy,
    val isLocal: Boolean,
    val track: TrackDetails,
)
