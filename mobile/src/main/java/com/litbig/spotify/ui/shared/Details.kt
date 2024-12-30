package com.litbig.spotify.ui.shared

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.litbig.spotify.ui.lifecycleIsResumed
import com.litbig.spotify.ui.shared.album.AlbumDetailScreen
import com.litbig.spotify.ui.shared.artist.ArtistDetailScreen
import com.litbig.spotify.ui.shared.playlist.PlaylistDetailScreen
import com.litbig.spotify.ui.shared.track.TracksScreen

sealed class DetailsSection(val route: String) {
    data object Album : DetailsSection("${ROUTE_ALBUM}/{${ARG_ALBUM_ID}}") {
        fun createRoute(albumId: String) = "${ROUTE_ALBUM}/$albumId"
    }

    data object Artist : DetailsSection("${ROUTE_ARTIST}/{${ARG_ARTIST_ID}}") {
        fun createRoute(artistId: String) = "${ROUTE_ARTIST}/$artistId"
    }

    data object Playlist : DetailsSection("${ROUTE_PLAYLIST}/{${ARG_PLAYLIST_ID}}") {
        fun createRoute(playlistId: String) = "${ROUTE_PLAYLIST}/$playlistId"
    }

    data object Tracks : DetailsSection("${ROUTE_TRACKS}/{${ARG_PLAYLIST_ID}}") {
        fun createRoute(playlistId: String) = "${ROUTE_TRACKS}/${playlistId}"
    }

    companion object {
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
fun rememberDetailsSectionState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current,
) = remember(navController, context) {
    DetailsSectionState(navController, context)
}

class DetailsSectionState(
    val navController: NavHostController,
    private val context: Context,
) {
    fun navigateToAlbum(albumId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(DetailsSection.Album.createRoute(albumId))
        }
    }

    fun navigateToArtist(artistId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(DetailsSection.Artist.createRoute(artistId))
        }
    }

    fun navigateToPlaylist(playlistId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(DetailsSection.Playlist.createRoute(playlistId))
        }
    }

    fun navigateToTracks(playlistId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(DetailsSection.Tracks.createRoute(playlistId))
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}

fun NavGraphBuilder.addDetailsGraph(
    modifier: Modifier = Modifier,
    navigateToAlbum: (String, NavBackStackEntry) -> Unit,
    navigateToArtist: (String, NavBackStackEntry) -> Unit,
    navigateToPlaylist: (String, NavBackStackEntry) -> Unit,
    navigateToTracks: (String, NavBackStackEntry) -> Unit,
    navigateBack: () -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    composable(DetailsSection.Album.route) { from ->
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

    composable(DetailsSection.Artist.route) { from ->
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

    composable(DetailsSection.Playlist.route) { from ->
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

    composable(DetailsSection.Tracks.route) { from ->
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