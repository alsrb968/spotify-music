package com.litbig.spotify.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.litbig.spotify.ui.grid.GridScreen
import com.litbig.spotify.ui.list.ListScreen

@Composable
fun SpotifyApp(
    appState: SpotifyAppState = rememberSpotifyAppState()
) {
    NavHost(
        navController = appState.navController,
        startDestination = Screen.Grid.route
    ) {
        composable(Screen.Grid.route) { backStackEntry ->
            GridScreen(
                navigateToList = { album ->
                    appState.navigateToList(album.name, backStackEntry)
                }
            )
        }
        composable(Screen.List.route) {
            ListScreen(
                navigateBack = appState::navigateBack
            )
        }
    }
}