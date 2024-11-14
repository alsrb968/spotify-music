package com.litbig.spotify.core.domain.model

import android.graphics.Bitmap

data class Artist(
    val name: String,
    val albumArt: Bitmap?,
    val albumCount: Int,
    val musicCount: Int,
)