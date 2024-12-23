package com.litbig.spotify.core.domain.model.remote

data class UserProfile(
    val displayName: String?,
    val externalUrls: ExternalUrls,
    val followers: Followers,
    val href: String,
    val id: String,
    val images: List<ImageInfo>,
    val type: String,
    val uri: String,
)
