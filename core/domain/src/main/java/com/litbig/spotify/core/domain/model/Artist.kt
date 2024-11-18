package com.litbig.spotify.core.domain.model

data class Artist(
    val name: String,
    val imageUrl: String?,
    val albumCount: Int,
    val musicCount: Int,
)