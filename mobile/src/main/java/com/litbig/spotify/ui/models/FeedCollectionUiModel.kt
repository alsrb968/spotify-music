package com.litbig.spotify.ui.models

enum class FeedCollectionType {
    NEW_ALBUM_RELEASES,
    ALBUMS_OF_ARTISTS,
    ARTISTS,
}

data class FeedCollectionUiModel(
    val imageUrl: String? = null,
    val title: String,
    val titleType: FeedCollectionType,
    val feeds: List<FeedUiModel>,
)