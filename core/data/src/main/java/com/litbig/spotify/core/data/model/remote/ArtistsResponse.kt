package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class ArtistsResponse(
    @SerializedName("href") val href: String,
    @SerializedName("limit") val limit: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("offset") val offset: Int,
    @SerializedName("previous") val previous: String?,
    @SerializedName("total") val total: Int,
    @SerializedName("items") val items: List<ArtistDetailsResponse>
)
