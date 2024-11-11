package com.litbig.spotify.util

object ConvertExtensions {
    @JvmStatic
    fun Long.toHumanReadableDuration(): String {
        val seconds = this / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "$minutes:${remainingSeconds.toString().padStart(2, '0')}"
    }
}