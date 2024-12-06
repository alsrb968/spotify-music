package com.litbig.spotify.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.litbig.spotify.R
import com.litbig.spotify.ui.home.HomeSections
import com.litbig.spotify.ui.home.SpotifyBottomBar
import com.litbig.spotify.ui.home.addHomeGraph
import timber.log.Timber

@Composable
fun SpotifyApp(
   appState: SpotifyAppState = rememberSpotifyAppState()
) {
    if (appState.isOnline) {
        NavHost(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            navController = appState.navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) { backStackEntry ->
                MainContainer(
                    onTrackSelected = { trackId, from ->
                        Timber.i("trackId: $trackId, from: $from")
                        appState.navigateToPlayer(trackId, from)
                    }
                )
            }

            composable(Screen.Player.route) { backStackEntry ->

            }
        }
    } else {
        OfflineDialog { appState.refreshOnline() }
    }
}

@Composable
fun MainContainer(
    modifier: Modifier = Modifier,
    onTrackSelected: (String, NavBackStackEntry) -> Unit
) {
    val appState = rememberSpotifyAppState()
    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
            SpotifyBottomBar(
                tabs = HomeSections.entries,
                currentRoute = currentRoute ?: HomeSections.FEED.route,
                navigateToRoute = appState::navigateToBottomBarRoute
            )
        }
    ) { padding ->
        NavHost(
            navController = appState.navController,
            startDestination = HomeSections.FEED.route
        ) {
            addHomeGraph(
                modifier = modifier
                    .padding(padding)
                    .consumeWindowInsets(padding),
                onTrackSelected = onTrackSelected
            )
        }
    }
}

@Composable
fun OfflineDialog(onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.internet_error_title)) },
        text = { Text(text = stringResource(R.string.internet_error_content)) },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(text = stringResource(R.string.retry))
            }
        }
    )
}