package com.litbig.spotify.core.domain.model.remote

data class AddedBy(
    val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    val type: String,
    val uri: String,
)
