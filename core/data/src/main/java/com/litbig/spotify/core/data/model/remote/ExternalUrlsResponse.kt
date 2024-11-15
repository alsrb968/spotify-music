package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class ExternalUrlsResponse(
    @SerializedName("spotify") val spotify: String
)