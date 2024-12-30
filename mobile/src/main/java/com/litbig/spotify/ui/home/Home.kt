package com.litbig.spotify.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.litbig.spotify.ui.home.feed.FeedScreen
import com.litbig.spotify.ui.shared.addDetailsGraph
import com.litbig.spotify.ui.shared.rememberDetailsSectionState

sealed class HomeSection(val route: String) {
    data object Feed : HomeSection(ROUTE_FEED)

    companion object {
        const val ROUTE_FEED = "feed"
    }
}

@Composable
fun HomeContainer(
    modifier: Modifier = Modifier,
    onShowSnackBar: (String) -> Unit,
) {
    val appState = rememberDetailsSectionState()

    NavHost(
        modifier = modifier,
        navController = appState.navController,
        startDestination = HomeSection.Feed.route
    ) {
        composable(HomeSection.Feed.route) { from ->
            FeedScreen(
                modifier = modifier,
                onAlbumSelected = { albumId ->
                    appState.navigateToAlbum(albumId, from)
                },
                onArtistSelected = { artistId ->
                    appState.navigateToArtist(artistId, from)
                },
                onShowSnackBar = onShowSnackBar
            )
        }

        addDetailsGraph(
            navigateToAlbum = { albumId, from ->
                appState.navigateToAlbum(albumId, from)
            },
            navigateToArtist = { artistId, from ->
                appState.navigateToArtist(artistId, from)
            },
            navigateToPlaylist = { playlistId, from ->
                appState.navigateToPlaylist(playlistId, from)
            },
            navigateToTracks = { playlistId, from ->
                appState.navigateToTracks(playlistId, from)
            },
            navigateBack = appState::navigateBack,
            onShowSnackBar = onShowSnackBar
        )
    }
}