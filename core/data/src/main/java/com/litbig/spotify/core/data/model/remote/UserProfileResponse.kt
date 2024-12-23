package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("external_urls") val externalUrls: ExternalUrlsResponse,
    @SerializedName("followers") val followers: FollowersResponse,
    @SerializedName("href") val href: String,
    @SerializedName("id") val id: String,
    @SerializedName("images") val images: List<ImageInfoResponse>,
    @SerializedName("type") val type: String,
    @SerializedName("uri") val uri: String,
)
