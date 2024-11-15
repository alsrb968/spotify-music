package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class FollowersResponse(
    @SerializedName("href") val href: String?,
    @SerializedName("total") val total: Int,
)
