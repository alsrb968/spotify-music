package com.litbig.spotify.ui.tooling

import androidx.compose.ui.graphics.Color
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.model.remote.ExternalUrls
import com.litbig.spotify.core.domain.model.remote.Followers
import com.litbig.spotify.ui.home.feed.FeedItem
import com.litbig.spotify.ui.home.feed.FeedCollection
import com.litbig.spotify.ui.home.album.AlbumDetailUiState
import com.litbig.spotify.ui.home.album.TrackInfo
import com.litbig.spotify.ui.player.TrackDetailsInfo

val PreviewFeedItem = FeedItem(
    id = "1",
    imageUrl = null,
    name = "Happier Than Ever (2021) by Billie Eilish with 16 tracks",
    type = "album",
)

val PreviewFeedCollection = FeedCollection(
    title = "Billie Eilish's Albums",
    feeds = List(10) { PreviewFeedItem },
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

val PreviewArtistDetails = ArtistDetails(
    externalUrls = ExternalUrls(spotify = "https://open.spotify.com/artist/4GNC7GD6oZMSxPGyXy4MNB"),
    followers = Followers(href = null, total = 1000000),
    genres = listOf("pop", "rock"),
    href = "https://api.spotify.com/v1/artists/4GNC7GD6oZMSxPGyXy4MNB",
    id = "4GNC7GD6oZMSxPGyXy4MNB",
    images = listOf(),
    name = "Billie Eilish",
    popularity = 100,
    type = "artist",
    uri = "spotify:artist:4GNC7GD6oZMSxPGyXy4MNB",
)

val PreviewArtistDetailsList = List(10) { PreviewArtistDetails }