package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class PlaylistTrackDetailsResponse(
    @SerializedName("added_at") val addedAt: String,
    @SerializedName("added_by") val addedBy: AddedByResponse,
    @SerializedName("is_local") val isLocal: Boolean,
    @SerializedName("track") val track: TrackDetailsResponse
)
