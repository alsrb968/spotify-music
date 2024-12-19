package com.litbig.spotify.core.domain.model.remote

data class PlaylistDetails(
    val collaborative: Boolean,
    val description: String,
    val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    val images: List<ImageInfo>,
    val name: String,
    val owner: Owner,
    val public: Boolean,
    val snapshotId: String,
    val tracks: Tracks,
    val type: String,
    val uri: String,
    val primaryColor: String?,
)
