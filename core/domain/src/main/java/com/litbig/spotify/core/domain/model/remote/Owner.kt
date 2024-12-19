package com.litbig.spotify.core.domain.model.remote

data class Owner(
    val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    val type: String,
    val uri: String,
    val displayName: String,
)
