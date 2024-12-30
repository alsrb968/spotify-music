package com.litbig.spotify.ui.library

import android.content.Context
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
import com.litbig.spotify.ui.home.HomeSection
import com.litbig.spotify.ui.shared.album.AlbumDetailScreen
import com.litbig.spotify.ui.shared.artist.ArtistDetailScreen
import com.litbig.spotify.ui.home.feed.FeedScreen
import com.litbig.spotify.ui.shared.playlist.PlaylistDetailScreen
import com.litbig.spotify.ui.shared.track.TracksScreen
import com.litbig.spotify.ui.lifecycleIsResumed


sealed class LibrarySection(val route: String) {
    data object Library : LibrarySection(ROUTE_LIBRARY)
    data object Album : LibrarySection("${ROUTE_ALBUM}/{${ARG_ALBUM_ID}}") {
        fun createRoute(albumId: String) = "${ROUTE_ALBUM}/$albumId"
    }

    data object Artist : LibrarySection("${ROUTE_ARTIST}/{${ARG_ARTIST_ID}}") {
        fun createRoute(artistId: String) = "${ROUTE_ARTIST}/$artistId"
    }

    data object Playlist : LibrarySection("${ROUTE_PLAYLIST}/{${ARG_PLAYLIST_ID}}") {
        fun createRoute(playlistId: String) = "${ROUTE_PLAYLIST}/$playlistId"
    }

    data object Tracks : LibrarySection("${ROUTE_TRACKS}/{${ARG_PLAYLIST_ID}}") {
        fun createRoute(playlistId: String) = "${ROUTE_TRACKS}/${playlistId}"
    }

    companion object {
        const val ROUTE_LIBRARY = "library"
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
fun rememberLibrarySectionState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current,
) = remember(navController, context) {
    LibrarySectionState(navController, context)
}

class LibrarySectionState(
    val navController: NavHostController,
    private val context: Context
) {
    fun navigateToAlbum(albumId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(LibrarySection.Album.createRoute(albumId))
        }
    }

    fun navigateToArtist(artistId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(LibrarySection.Artist.createRoute(artistId))
        }
    }

    fun navigateToPlaylist(playlistId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(LibrarySection.Playlist.createRoute(playlistId))
        }
    }

    fun navigateToTracks(playlistId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(LibrarySection.Tracks.createRoute(playlistId))
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}

@Composable
fun LibraryContainer(
    modifier: Modifier = Modifier,
    onShowSnackBar: (String) -> Unit
) {
    val appState = rememberLibrarySectionState()

    NavHost(
        modifier = modifier,
        navController = appState.navController,
        startDestination = LibrarySection.Library.route,
    ) {
        addLibraryGraph(
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

fun NavGraphBuilder.addLibraryGraph(
    modifier: Modifier = Modifier,
    navigateToAlbum: (String, NavBackStackEntry) -> Unit,
    navigateToArtist: (String, NavBackStackEntry) -> Unit,
    navigateToPlaylist: (String, NavBackStackEntry) -> Unit,
    navigateToTracks: (String, NavBackStackEntry) -> Unit,
    navigateBack: () -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    composable(LibrarySection.Library.route) { from ->
        LibraryScreen(
            modifier = modifier,
            navigateToAlbum = { albumId ->
                navigateToAlbum(albumId, from)
            },
            navigateToArtist = { artistId ->
                navigateToArtist(artistId, from)
            },
            navigateToPlaylist = { playlistId ->
                navigateToPlaylist(playlistId, from)
            }
        )
    }

    composable(LibrarySection.Album.route) { from ->
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

    composable(LibrarySection.Artist.route) { from ->
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

    composable(LibrarySection.Playlist.route) { from ->
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

    composable(LibrarySection.Tracks.route) { from ->
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