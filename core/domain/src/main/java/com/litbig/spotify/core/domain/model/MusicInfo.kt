package com.litbig.spotify.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicInfo(
    val imageUrl: String?,
    val title: String,
    val content: String,
    val category: String,
)
