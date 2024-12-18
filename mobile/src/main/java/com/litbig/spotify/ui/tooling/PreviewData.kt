package com.litbig.spotify.ui.tooling

import androidx.compose.ui.graphics.Color
import com.litbig.spotify.ui.models.*

val PreviewFeedUiModel = FeedUiModel(
    id = "1",
    imageUrl = null,
    name = "Happier Than Ever (2021) by Billie Eilish with 16 tracks",
    type = "album",
)

val PreviewFeedCollectionUiModel = FeedCollectionUiModel(
    title = "Billie Eilish's Albums",
    feeds = List(10) { PreviewFeedUiModel },
)

val PreviewFeedCollectionUiModels = List(10) { PreviewFeedCollectionUiModel }


val PreviewTrackUiModel = TrackUiModel(
    id = "1",
    imageUrl = null,
    name = "Happier Than Ever",
    artists = "Billie Eilish",
    duration = 192000L,
)

val PreviewTrackUiModels = List(10) { PreviewTrackUiModel }

val PreviewAlbumUiModel = AlbumUiModel(
    id = "1",
    imageUrl = null,
    name = "Happier Than Ever",
    artists = "Billie Eilish",
    totalTime = 192000L,
    dominantColor = Color.Transparent,
)

val PreviewArtistUiModel = ArtistUiModel(
    id = "1",
    imageUrl = null,
    name = "Billie Eilish",
    follower = 1000000,
    popularity = 100,
    genres = "Pop, Indie",
    dominantColor = Color.Transparent,
)