package com.litbig.spotify.ui.tooling

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.litbig.spotify.R
import com.litbig.spotify.ui.models.*

@Composable
fun PreviewAlbumArt(): Painter = painterResource(id = R.drawable.preview_album)

val PreviewLoremIpsum = """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras ullamcorper pharetra massa,
        sed suscipit nunc mollis in. Sed tincidunt orci lacus, vel ullamcorper nibh congue quis.
        Etiam imperdiet facilisis ligula id facilisis. Suspendisse potenti. Cras vehicula neque sed
        nulla auctor scelerisque. Vestibulum at congue risus, vel aliquet eros. In arcu mauris,
        facilisis eget magna quis, rhoncus volutpat mi. Phasellus vel sollicitudin quam, eu
        consectetur dolor. Proin lobortis venenatis sem, in vestibulum est. Duis ac nibh interdum,
        """.trimIndent()

val PreviewFeedUiModel = FeedUiModel(
    id = "1",
    imageUrl = null,
    name = "Happier Than Ever (2021) by Billie Eilish with 16 tracks",
    type = "album",
)

val PreviewFeedCollectionUiModel = FeedCollectionUiModel(
    title = "Billie Eilish's Albums",
    titleType = FeedCollectionType.NEW_ALBUM_RELEASES,
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
    albumType = "album",
    name = "Happier Than Ever",
    artists = "Billie Eilish",
    totalTime = 192000L,
    releaseDate = 1734696600000,
    copyright = "\u00A9 2021 Universal Music Group\n\u2117 2021 Universal Music Group",
    dominantColor = Color.Transparent,
)

val PreviewAlbumUiModels = List(10) { PreviewAlbumUiModel }

val PreviewArtistUiModel = ArtistUiModel(
    id = "1",
    imageUrl = null,
    name = "Billie Eilish",
    follower = 1000000,
    popularity = 100,
    genres = "Pop, Indie",
    dominantColor = Color.Transparent,
)

val PreviewArtistUiModels = List(10) { PreviewArtistUiModel }

val PreviewPlaylistUiModel = PlaylistUiModel(
    id = "1",
    imageUrl = null,
    name = "Happier Than Ever",
    follower = 1000000,
    description = "Billie Eilish's playlist",
    dominantColor = Color.Transparent,
)

val PreviewPlaylistUiModels = List(10) { PreviewPlaylistUiModel }

val PreviewOwnerUiModel = OwnerUiModel(
    id = "1",
    imageUrl = null,
    name = "Billie Eilish",
    follower = 1000000,
)