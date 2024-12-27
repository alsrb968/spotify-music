package com.litbig.spotify.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.ui.home.HomeContainer
import com.litbig.spotify.ui.library.LibraryScreen
import com.litbig.spotify.ui.player.PlayerBar
import com.litbig.spotify.ui.search.SearchScreen
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SpotifyApp(
    modifier: Modifier = Modifier,
    appState: SpotifyAppState = rememberSpotifyAppState()
) {
    if (appState.isOnline) {
        val coroutineScope = rememberCoroutineScope()
        val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            modifier = modifier,
            bottomBar = {
                Column {
                    PlayerBar(
                        onShowSnackBar = { message ->
                            coroutineScope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                    SpotifyBottomBar(
                        tabs = Screen.screens,
                        currentRoute = currentRoute ?: Screen.Home.route,
                        navigateToRoute = appState::navigateToBottomBarRoute
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        ) { padding ->
            NavHost(
                modifier = modifier
                    .consumeWindowInsets(padding)
                    .background(MaterialTheme.colorScheme.background),
                navController = appState.navController,
                startDestination = Screen.Library.route
            ) {
                composable(Screen.Home.route) { backStackEntry ->
                    HomeContainer(
                        modifier = modifier,
                        onShowSnackBar = { message ->
                            coroutineScope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                }

                composable(Screen.Search.route) { from ->
                    SearchScreen(
                        modifier = modifier,
                        onTrackClick = { trackId -> }
                    )
                }

                composable(Screen.Library.route) { from ->
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

                composable(Screen.Premium.route) { from ->

                }
            }
        }
    } else {
        OfflineDialog { appState.refreshOnline() }
    }
}

@Composable
fun SpotifyBottomBar(
    tabs: List<Screen>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    val routes = remember { tabs.map { it.route } }
    val currentSection = tabs.first { it.route == currentRoute }

    AnimatedVisibility(
        visible = tabs.map { it.route }.contains(currentRoute)
    ) {
        NavigationBar(
            modifier = modifier
                .height(85.dp)
                .gradientBackground(
                    ratio = 0.7f,
                    startColor = Color.Transparent,
                    endColor = MaterialTheme.colorScheme.background
                ),
            containerColor = color,
            contentColor = contentColor,
        ) {
            val configuration = LocalConfiguration.current
            val currentLocale: Locale =
                ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()

            tabs.forEach { screen ->
                val selected = screen == currentSection
                val tint by animateColorAsState(
                    if (selected) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.secondary
                    },
                    label = "tint"
                )
                val text = stringResource(screen.title).uppercase(currentLocale)

                NavigationBarItem(
                    icon = {
                        val iconSelected = ImageVector.vectorResource(screen.icons.first)
                        val iconNormal = ImageVector.vectorResource(screen.icons.second)
                        Icon(
                            imageVector = screen.icons.let { if (selected) iconNormal else iconSelected },
                            tint = tint,
                            contentDescription = text
                        )
                    },
                    label = {
                        Text(
                            text = text,
                            color = tint,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                    },
                    selected = selected,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = tint,
                        selectedTextColor = tint,
                        indicatorColor = Color.Transparent
                    ),
                    onClick = { navigateToRoute(screen.route) },
                )
            }
        }
    }
}


@DevicePreviews
@Composable
private fun SpotifyBottomBarPreview() {
    SpotifyTheme {
        SpotifyBottomBar(
            tabs = Screen.screens,
            currentRoute = Screen.Home.route,
            navigateToRoute = { }
        )
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

@DevicePreviews
@Composable
private fun OfflineDialogPreview() {
    SpotifyTheme {
        OfflineDialog { }
    }
}