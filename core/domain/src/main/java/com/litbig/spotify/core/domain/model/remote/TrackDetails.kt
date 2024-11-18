package com.litbig.spotify.core.domain.model.remote

data class TrackDetails(
    val album: AlbumDetails,
    val artists: List<ArtistDetails>,
    val discNumber: Int,
    val durationMs: Int,
    val explicit: Boolean,
    val externalIds: ExternalIds,
    val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    val isPlayable: Boolean,
    val linkedFrom: LinkedFrom?,
    val name: String,
    val popularity: Int,
    val previewUrl: String?,
    val trackNumber: Int,
    val type: String,
    val uri: String,
    val isLocal: Boolean
)
