package com.litbig.spotify.core.domain.model

data class Search(
    val tracks: Tracks?,
    val artists: Artists?,
    val albums: Albums?,
)
