@file:OptIn(ExperimentalMaterial3Api::class)

package com.litbig.spotify.ui.home

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.litbig.spotify.R
import com.litbig.spotify.ui.Screen
import com.litbig.spotify.ui.home.feed.FeedContainer
import com.litbig.spotify.ui.home.search.SearchScreen
import com.litbig.spotify.ui.player.PlayerBottomSheet
import com.litbig.spotify.ui.player.PlayerUiState
import com.litbig.spotify.ui.rememberSpotifyAppState
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import kotlinx.coroutines.launch
import java.util.Locale

sealed class HomeSection(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    data object Feed : HomeSection(
        title = R.string.home_feed,
        icon = Icons.Outlined.Home,
        route = ROUTE_FEED
    )

    data object Search : HomeSection(
        title = R.string.home_search,
        icon = Icons.Outlined.Search,
        route = ROUTE_SEARCH
    )

    data object Library : HomeSection(
        title = R.string.home_library,
        icon = Icons.Outlined.LibraryMusic,
        route = ROUTE_LIBRARY
    )

    data object Premium : HomeSection(
        title = R.string.home_premium,
        icon = Icons.Outlined.AcUnit,
        route = ROUTE_PREMIUM
    )

    companion object {
        const val ROUTE_FEED = "${Screen.ROUTE_HOME}/feed"
        const val ROUTE_SEARCH = "${Screen.ROUTE_HOME}/search"
        const val ROUTE_LIBRARY = "${Screen.ROUTE_HOME}/library"
        const val ROUTE_PREMIUM = "${Screen.ROUTE_HOME}/premium"

        val sections = listOf(Feed, Search, Library, Premium)
    }
}

fun NavGraphBuilder.addHomeGraph(
    modifier: Modifier = Modifier,
    onTrackSelected: (String, NavBackStackEntry) -> Unit
) {
    composable(HomeSection.Feed.route) { from ->
        FeedContainer(
            modifier = modifier,
            onTrackSelected = onTrackSelected
        )
    }

    composable(HomeSection.Search.route) { from ->
        SearchScreen(
            modifier = modifier,
            onTrackClick = { trackId -> onTrackSelected(trackId, from) }
        )
    }

    composable(HomeSection.Library.route) { from ->

    }

    composable(HomeSection.Premium.route) { from ->

    }
}

@Composable
fun HomeContainer(
    modifier: Modifier = Modifier,
    onTrackSelected: (String, NavBackStackEntry) -> Unit
) {
    val appState = rememberSpotifyAppState()
    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    PlayerBottomSheet {

        Scaffold(
            modifier = modifier,
            bottomBar = {
                SpotifyBottomBar(
                    tabs = HomeSection.sections,
                    currentRoute = currentRoute ?: HomeSection.Feed.route,
                    navigateToRoute = appState::navigateToBottomBarRoute
                )
            }
        ) { padding ->
            NavHost(
                modifier = Modifier
                    .padding(padding),
                navController = appState.navController,
                startDestination = HomeSection.Feed.route
            ) {
                addHomeGraph(
                    modifier = Modifier
                        .consumeWindowInsets(padding),
                    onTrackSelected = onTrackSelected
                )
            }
        }
    }

}

@Composable
fun SpotifyBottomBar(
    tabs: List<HomeSection>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    val routes = remember { tabs.map { it.route } }
    val currentSection = tabs.first { it.route == currentRoute }

    AnimatedVisibility(
        visible = tabs.map { it.route }.contains(currentRoute)
    ) {
        NavigationBar(
            modifier = modifier,
            containerColor = color,
            contentColor = contentColor,
        ) {
            val configuration = LocalConfiguration.current
            val currentLocale: Locale =
                ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()

            tabs.forEach { section ->
                val selected = section == currentSection
                val tint by animateColorAsState(
                    if (selected) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.secondary
                    },
                    label = "tint"
                )
                val text = stringResource(section.title).uppercase(currentLocale)

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = section.icon,
                            tint = tint,
                            contentDescription = text
                        )
                    },
                    label = {
                        Text(
                            text = text,
                            color = tint,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1
                        )
                    },
                    selected = selected,
                    onClick = { navigateToRoute(section.route) },
                )
            }
        }
    }
}


@DevicePreviews
@Composable
fun PreviewSpotifyBottomBar() {
    SpotifyTheme {
        SpotifyBottomBar(
            tabs = HomeSection.sections,
            currentRoute = HomeSection.Feed.route,
            navigateToRoute = { }
        )
    }
}