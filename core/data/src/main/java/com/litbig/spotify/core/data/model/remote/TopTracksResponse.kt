package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class TopTracksResponse(
    @SerializedName("tracks") val tracks: List<TrackDetailsResponse>
)
