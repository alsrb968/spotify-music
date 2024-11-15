package com.litbig.spotify.core.domain.model

data class Albums(
    val href: String,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int,
    val items: List<AlbumDetails>
)
