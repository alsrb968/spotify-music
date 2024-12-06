package com.litbig.spotify.ui.home.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.ui.components.AlbumCollection
import com.litbig.spotify.ui.theme.SpotifyTheme

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
                modifier = modifier,
                feedCollection = feedCollection,
                onAlbum = onAlbum,
                onMore = {}
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun Loading(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
fun LoadingPreview() {
    SpotifyTheme {
        Loading()
    }
}