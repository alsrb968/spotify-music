package com.litbig.spotify.ui.home

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.litbig.spotify.ui.shared.album.AlbumDetailScreen
import com.litbig.spotify.ui.shared.artist.ArtistDetailScreen
import com.litbig.spotify.ui.home.feed.FeedScreen
import com.litbig.spotify.ui.shared.playlist.PlaylistDetailScreen
import com.litbig.spotify.ui.shared.track.TracksScreen
import com.litbig.spotify.ui.lifecycleIsResumed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class HomeSection(val route: String) {
    data object Feed : HomeSection(ROUTE_FEED)
    data object Album : HomeSection("${ROUTE_ALBUM}/{${ARG_ALBUM_ID}}") {
        fun createRoute(albumId: String) = "${ROUTE_ALBUM}/$albumId"
    }

    data object Artist : HomeSection("${ROUTE_ARTIST}/{${ARG_ARTIST_ID}}") {
        fun createRoute(artistId: String) = "${ROUTE_ARTIST}/$artistId"
    }

    data object Playlist : HomeSection("${ROUTE_PLAYLIST}/{${ARG_PLAYLIST_ID}}") {
        fun createRoute(playlistId: String) = "${ROUTE_PLAYLIST}/$playlistId"
    }

    data object Tracks : HomeSection("${ROUTE_TRACKS}/{${ARG_PLAYLIST_ID}}") {
        fun createRoute(playlistId: String) = "${ROUTE_TRACKS}/${playlistId}"
    }

    companion object {
        const val ROUTE_FEED = "list"
        const val ROUTE_ALBUM = "album"
        const val ROUTE_ARTIST = "artist"
        const val ROUTE_PLAYLIST = "playlist"
        const val ROUTE_TRACKS = "tracks"

        const val ARG_ALBUM_ID = "album_id"
        const val ARG_ARTIST_ID = "artist_id"
        const val ARG_PLAYLIST_ID = "playlist_id"
    }
}

@Composable
fun rememberHomeSectionState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current,
) = remember(navController, context) {
    HomeSectionState(navController, context)
}

class HomeSectionState(
    val navController: NavHostController,
    private val context: Context,
) {
    fun navigateToAlbum(albumId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(HomeSection.Album.createRoute(Uri.encode(albumId)))
        }
    }

    fun navigateToArtist(artistId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(HomeSection.Artist.createRoute(Uri.encode(artistId)))
        }
    }

    fun navigateToPlaylist(playlistId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(HomeSection.Playlist.createRoute(Uri.encode(playlistId)))
        }
    }

    fun navigateToTracks(playlistId: String, from: NavBackStackEntry) {
        CoroutineScope(Dispatchers.Main).launch {
            if (from.lifecycleIsResumed()) {
                navController.navigate(HomeSection.Tracks.createRoute(Uri.encode(playlistId)))
            }
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}

@Composable
fun HomeContainer(
    modifier: Modifier = Modifier,
    onShowSnackBar: (String) -> Unit,
) {
    val appState = rememberHomeSectionState()

    NavHost(
        modifier = modifier,
        navController = appState.navController,
        startDestination = HomeSection.Feed.route
    ) {
        addHomeGraph(
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

fun NavGraphBuilder.addHomeGraph(
    modifier: Modifier = Modifier,
    navigateToAlbum: (String, NavBackStackEntry) -> Unit,
    navigateToArtist: (String, NavBackStackEntry) -> Unit,
    navigateToPlaylist: (String, NavBackStackEntry) -> Unit,
    navigateToTracks: (String, NavBackStackEntry) -> Unit,
    navigateBack: () -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    composable(HomeSection.Feed.route) { from ->
        FeedScreen(
            modifier = modifier,
            onAlbumSelected = { albumId ->
                navigateToAlbum(albumId, from)
            },
            onArtistSelected = { artistId ->
                navigateToArtist(artistId, from)
            },
            onShowSnackBar = onShowSnackBar
        )
    }

    composable(HomeSection.Album.route) { from ->
        AlbumDetailScreen(
            modifier = modifier,
            navigateToAlbum = { albumId ->
                navigateToAlbum(albumId, from)
            },
            navigateToArtist = { artistId ->
                navigateToArtist(artistId, from)
            },
            navigateToPlaylist = { playlistId ->
                navigateToPlaylist(playlistId, from)
            },
            navigateBack = navigateBack,
            onShowSnackBar = onShowSnackBar
        )
    }

    composable(HomeSection.Artist.route) { from ->
        ArtistDetailScreen(
            modifier = modifier,
            navigateToAlbum = { albumId ->
                navigateToAlbum(albumId, from)
            },
            navigateToArtist = { artistId ->
                navigateToArtist(artistId, from)
            },
            navigateToPlaylist = { playlistId ->
                navigateToPlaylist(playlistId, from)
            },
            navigateBack = navigateBack,
            onShowSnackBar = onShowSnackBar
        )
    }

    composable(HomeSection.Playlist.route) { from ->
        PlaylistDetailScreen(
            modifier = modifier,
            navigateToPlaylist = { playlistId ->
                navigateToPlaylist(playlistId, from)
            },
            navigateToTracks = { playlistId ->
                navigateToTracks(playlistId, from)
            },
            navigateBack = navigateBack,
            onShowSnackBar = onShowSnackBar
        )
    }

    composable(HomeSection.Tracks.route) { from ->
        TracksScreen(
            modifier = modifier,
            navigateToArtist = { artistId ->
                navigateToArtist(artistId, from)
            },
            navigateBack = navigateBack,
            onShowSnackBar = onShowSnackBar
        )
    }
}