package com.litbig.spotify.ui.tooling

import androidx.compose.ui.graphics.Color
import com.litbig.spotify.ui.home.feed.FeedAlbum
import com.litbig.spotify.ui.home.feed.FeedCollection
import com.litbig.spotify.ui.home.feed.album.AlbumDetailUiState
import com.litbig.spotify.ui.home.feed.album.TrackInfo
import com.litbig.spotify.ui.player.TrackDetailsInfo

val PreviewFeedAlbum = FeedAlbum(
    id = "1",
    imageUrl = null,
    name = "Happier Than Ever (2021) by Billie Eilish with 16 tracks",
)

val PreviewFeedCollection = FeedCollection(
    title = "Billie Eilish's Albums",
    feeds = List(10) { PreviewFeedAlbum },
)

val PreviewFeedCollections = List(10) { PreviewFeedCollection }

val PreviewAlbumDetailUiState = AlbumDetailUiState.Ready(
    imageUrl = null,
    albumName = "Happier Than Ever",
    artistNames = "Billie Eilish",
    totalTime = 1000L,
    trackInfos = List(10) {
        TrackInfo(
            id = "1",
            imageUrl = null,
            title = "Happier Than Ever",
            artist = "Billie Eilish",
        )
    },
    dominantColor = Color.Transparent,
    playingTrackId = null,
)

val PreviewTrackDetailsInfo = TrackDetailsInfo(
    id = "1",
    imageUrl = null,
    title = "Happier Than Ever",
    artist = "Billie Eilish",
    duration = 192000L,
)

val PreviewTrackDetailsInfos = List(10) { PreviewTrackDetailsInfo }

