package com.litbig.spotify.core.domain.extension

import kotlin.time.Duration

fun Duration.toHumanReadableDuration(): String {
    val minutes = this.inWholeMinutes
    val seconds = this.inWholeSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

fun Long.toHumanReadableDuration(): String {
    val secs = this / 1000
    val minutes = secs / 60
    val seconds = secs % 60
    return "%02d:%02d".format(minutes, seconds)
}