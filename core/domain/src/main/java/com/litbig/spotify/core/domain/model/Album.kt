package com.litbig.spotify.core.domain.model

data class Album(
    val name: String,
    val artist: String,
    val imageUrl: String?,
    val musicCount: Int,
) : java.io.Serializable
