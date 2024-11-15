package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class ArtistDetailsResponse(
    @SerializedName("external_urls") val externalUrls: ExternalUrlsResponse,
    @SerializedName("followers") val followers: FollowersResponse,
    @SerializedName("genres") val genres: List<String>,
    @SerializedName("href") val href: String,
    @SerializedName("id") val id: String,
    @SerializedName("images") val images: List<ImageResponse>,
    @SerializedName("name") val name: String,
    @SerializedName("popularity") val popularity: Int,
    @SerializedName("type") val type: String,
    @SerializedName("uri") val uri: String,
)
