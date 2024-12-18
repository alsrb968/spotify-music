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
import com.litbig.spotify.ui.home.artist.ArtistDetailScreen
import com.litbig.spotify.ui.rememberSpotifyAppState

sealed class HomeSection(val route: String) {
    data object Feed : HomeSection(ROUTE_FEED)
    data object Album : HomeSection("${ROUTE_ALBUM}/{${ARG_ALBUM_ID}}") {
        fun createRoute(albumId: String) = "${ROUTE_ALBUM}/$albumId"
    }

    data object Artist : HomeSection("${ROUTE_ARTIST}/{${ARG_ARTIST_ID}}") {
        fun createRoute(artistId: String) = "${ROUTE_ARTIST}/$artistId"
    }

    companion object {
        const val ROUTE_FEED = "${Screen.ROUTE_HOME}/list"
        const val ROUTE_ALBUM = "${Screen.ROUTE_HOME}/album"
        const val ROUTE_ARTIST = "${Screen.ROUTE_HOME}/artist"

        const val ARG_ALBUM_ID = "album_id"
        const val ARG_ARTIST_ID = "artist_id"
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
            onArtistSelected = { artistId, from ->
                appState.navigateToArtist(artistId, from)
            },
            navigateBack = appState::navigateBack,
            onShowSnackBar = onShowSnackBar
        )
    }
}

fun NavGraphBuilder.addHomeGraph(
    modifier: Modifier = Modifier,
    onAlbumSelected: (String, NavBackStackEntry) -> Unit,
    onArtistSelected: (String, NavBackStackEntry) -> Unit,
    navigateBack: () -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    composable(HomeSection.Feed.route) { from ->
        FeedScreen(
            modifier = modifier,
            onAlbumSelected = { albumId ->
                onAlbumSelected(albumId, from)
            },
            onArtistSelected = { artistId ->
                onArtistSelected(artistId, from)
            },
            onShowSnackBar = onShowSnackBar
        )
    }

    composable(HomeSection.Album.route) { from ->
        AlbumDetailScreen(
            modifier = modifier,
            navigateBack = navigateBack,
            onShowSnackBar = onShowSnackBar
        )
    }

    composable(HomeSection.Artist.route) { from ->
        ArtistDetailScreen(
            modifier = modifier,
            navigateBack = navigateBack,
            onShowSnackBar = onShowSnackBar
        )
    }
}