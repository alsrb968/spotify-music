package com.litbig.spotify.ui.home.feed

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.litbig.spotify.ui.components.AlbumCollection
import com.litbig.spotify.ui.home.HomeSection
import com.litbig.spotify.ui.home.feed.album.AlbumDetailScreen
import com.litbig.spotify.ui.rememberSpotifyAppState
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewFeedCollections
import timber.log.Timber

sealed class FeedSection(val route: String) {
    data object List : FeedSection(ROUTE_LIST)
    data object Album : FeedSection("${ROUTE_ALBUM}/{${ARG_ALBUM_ID}}") {
        fun createRoute(albumId: String) = "${ROUTE_ALBUM}/$albumId"
    }

    companion object {
        const val ROUTE_LIST = "${HomeSection.ROUTE_FEED}/list"
        const val ROUTE_ALBUM = "${HomeSection.ROUTE_FEED}/album"

        const val ARG_ALBUM_ID = "album_id"
    }
}

fun NavGraphBuilder.addFeedGraph(
    modifier: Modifier = Modifier,
    onTrackSelected: (String, NavBackStackEntry) -> Unit,
    onAlbumSelected: (String, NavBackStackEntry) -> Unit,
    navigateToBack: () -> Unit
) {
    composable(FeedSection.List.route) { from ->
        FeedScreen(
            modifier = modifier,
            onTrackSelected = { trackId ->
                onTrackSelected(trackId, from)
            },
            onAlbumSelected = { albumId ->
                onAlbumSelected(albumId, from)
            }
        )
    }

    composable(FeedSection.Album.route) { from ->
        AlbumDetailScreen(
            modifier = modifier,
            navigateToBack = navigateToBack
        )
    }
}

@Composable
fun FeedContainer(
    modifier: Modifier = Modifier,
    onTrackSelected: (String, NavBackStackEntry) -> Unit,
) {
    val appState = rememberSpotifyAppState()

    NavHost(
        modifier = modifier,
        navController = appState.navController,
        startDestination = FeedSection.List.route
    ) {
        addFeedGraph(
            onTrackSelected = onTrackSelected,
            onAlbumSelected = { albumId, from ->
                appState.navigateToAlbum(albumId, from)
            },
            navigateToBack = appState::navigateBack
        )
    }
}


@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
    onTrackSelected: (String) -> Unit,
    onAlbumSelected: (String) -> Unit,
) {

    val state by viewModel.state.collectAsStateWithLifecycle()



    FeedScreen(
        modifier = modifier,
        feedCollections = state.feedCollections,
        onTrackSelected = onTrackSelected,
        onAlbumSelected = onAlbumSelected
    )
}

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    feedCollections: List<FeedCollection>,
    onTrackSelected: (String) -> Unit,
    onAlbumSelected: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(feedCollections.size) { index ->
            val feedCollection = feedCollections[index]
            AlbumCollection(
                feedCollection = feedCollection,
                onTrack = onTrackSelected,
                onAlbum = onAlbumSelected,
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
            onTrackSelected = {},
            onAlbumSelected = {}
        )
    }
}