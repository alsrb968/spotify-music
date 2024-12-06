package com.litbig.spotify.ui.home.feed

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.ui.components.AlbumCollection
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewFeedCollections
import timber.log.Timber

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
    onTrackClick: (String) -> Unit
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    FeedScreen(
        modifier = modifier,
        feedCollections = state.feedCollections,
        onAlbum = { albumId ->
            onTrackClick(albumId)
        }
    )
}

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    feedCollections: List<FeedCollection>,
    onAlbum: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(feedCollections.size) { index ->
            val feedCollection = feedCollections[index]
            AlbumCollection(
                feedCollection = feedCollection,
                onAlbum = onAlbum,
                onMore = {
                    Timber.i("onMore")
                }
            )
        }
    }
}

@DevicePreviews
@Composable
fun PreviewFeedScreen() {
    SpotifyTheme {
        FeedScreen(
            feedCollections = PreviewFeedCollections,
            onAlbum = {}
        )
    }
}