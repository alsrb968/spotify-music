package com.litbig.spotify.ui.tooling

import androidx.compose.ui.graphics.Color
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.model.remote.ExternalUrls
import com.litbig.spotify.core.domain.model.remote.Followers
import com.litbig.spotify.ui.home.album.AlbumUiModel
import com.litbig.spotify.ui.home.album.TrackUiModel
import com.litbig.spotify.ui.home.feed.FeedCollectionUiModel
import com.litbig.spotify.ui.home.feed.FeedUiModel

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