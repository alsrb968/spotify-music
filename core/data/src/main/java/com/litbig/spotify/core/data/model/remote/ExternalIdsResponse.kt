package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class ExternalIdsResponse(
    @SerializedName("isrc") val isrc: String?,
    @SerializedName("ean") val ean: String?,
    @SerializedName("upc") val upc: String?
)
