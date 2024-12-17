package com.litbig.spotify.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.litbig.spotify.ui.Screen
import com.litbig.spotify.ui.home.feed.FeedScreen
import com.litbig.spotify.ui.home.album.AlbumDetailScreen
import com.litbig.spotify.ui.rememberSpotifyAppState

sealed class HomeSection(val route: String) {
    data object Feed : HomeSection(ROUTE_FEED)
    data object Album : HomeSection("${ROUTE_ALBUM}/{${ARG_ALBUM_ID}}") {
        fun createRoute(albumId: String) = "${ROUTE_ALBUM}/$albumId"
    }

    companion object {
        const val ROUTE_FEED = "${Screen.ROUTE_HOME}/list"
        const val ROUTE_ALBUM = "${Screen.ROUTE_HOME}/album"

        const val ARG_ALBUM_ID = "album_id"
    }
}

@Composable
fun HomeContainer(
    modifier: Modifier = Modifier,
    onShowSnackBar: (String) -> Unit,
) {
    val appState = rememberSpotifyAppState()

    NavHost(
        modifier = modifier,
        navController = appState.navController,
        startDestination = HomeSection.Feed.route
    ) {
        addHomeGraph(
            onAlbumSelected = { albumId, from ->
                appState.navigateToAlbum(albumId, from)
            },
            navigateToBack = appState::navigateBack,
            onShowSnackBar = onShowSnackBar
        )
    }
}

fun NavGraphBuilder.addHomeGraph(
    modifier: Modifier = Modifier,
    onAlbumSelected: (String, NavBackStackEntry) -> Unit,
    navigateToBack: () -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    composable(HomeSection.Feed.route) { from ->
        FeedScreen(
            modifier = modifier,
            onAlbumSelected = { albumId ->
                onAlbumSelected(albumId, from)
            },
            onShowSnackBar = onShowSnackBar
        )
    }

    composable(HomeSection.Album.route) { from ->
        AlbumDetailScreen(
            modifier = modifier,
            navigateToBack = navigateToBack,
            onShowSnackBar = onShowSnackBar
        )
    }
}