package com.litbig.spotify.core.domain.model.remote

data class AlbumDetails(
    val albumType: String,
    val totalTracks: Int,
    val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    val images: List<ImageInfo>,
    val name: String,
    val releaseDate: String,
    val releaseDatePrecision: String,
    val type: String,
    val uri: String,
    val artists: List<ArtistDetails>,
    val tracks: Tracks?,
    val copyrights: List<Copyrights>?,
    val externalIds: ExternalIds?,
    val genres: List<String>?,
    val label: String?,
    val popularity: Int?,
    val isPlayable: Boolean
)
