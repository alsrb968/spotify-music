package com.litbig.spotify.ui

import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.litbig.spotify.R
import com.litbig.spotify.ui.home.HomeContainer
import com.litbig.spotify.ui.player.PlayerBottomSheet
import com.litbig.spotify.ui.player.PlayerScreen
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
                HomeContainer(
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