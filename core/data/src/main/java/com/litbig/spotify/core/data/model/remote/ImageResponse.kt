package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("url") val url: String,
    @SerializedName("height") val height: Int,
    @SerializedName("width") val width: Int,
)
