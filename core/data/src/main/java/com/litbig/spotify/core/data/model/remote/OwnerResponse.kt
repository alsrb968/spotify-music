package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class OwnerResponse(
    @SerializedName("external_urls") val externalUrls: ExternalUrlsResponse,
    @SerializedName("href") val href: String,
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("display_name") val displayName: String,
)
