package com.litbig.spotify.core.domain.model

import android.graphics.Bitmap

data class Album(
    val name: String,
    val artist: String,
    val albumArt: Bitmap?,
)
