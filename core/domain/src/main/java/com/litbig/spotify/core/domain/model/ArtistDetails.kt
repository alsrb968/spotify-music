package com.litbig.spotify.core.domain.model

data class ArtistDetails(
    val externalUrls: ExternalUrls,
    val followers: Followers?,
    val genres: List<String>?,
    val href: String,
    val id: String,
    val images: List<ImageInfo>?,
    val name: String,
    val popularity: Int?,
    val type: String,
    val uri: String,
)
