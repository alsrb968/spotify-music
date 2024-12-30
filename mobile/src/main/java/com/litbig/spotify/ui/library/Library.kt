package com.litbig.spotify.ui.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.litbig.spotify.ui.shared.addDetailsGraph
import com.litbig.spotify.ui.shared.rememberDetailsSectionState

sealed class LibrarySection(val route: String) {
    data object Library : LibrarySection(ROUTE_LIBRARY)

    companion object {
        const val ROUTE_LIBRARY = "library"
    }
}

@Composable
fun LibraryContainer(
    modifier: Modifier = Modifier,
    onShowSnackBar: (String) -> Unit
) {
    val appState = rememberDetailsSectionState()

    NavHost(
        modifier = modifier,
        navController = appState.navController,
        startDestination = LibrarySection.Library.route,
    ) {
        composable(LibrarySection.Library.route) { from ->
            LibraryScreen(
                modifier = modifier,
                navigateToAlbum = { albumId ->
                    appState.navigateToAlbum(albumId, from)
                },
                navigateToArtist = { artistId ->
                    appState.navigateToArtist(artistId, from)
                },
                navigateToPlaylist = { playlistId ->
                    appState.navigateToPlaylist(playlistId, from)
                }
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