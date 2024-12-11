@file:OptIn(ExperimentalMaterial3Api::class)

package com.litbig.spotify.ui.home

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
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
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.ui.Screen
import com.litbig.spotify.ui.home.feed.FeedContainer
import com.litbig.spotify.ui.home.search.SearchScreen
import com.litbig.spotify.ui.player.PlayerBar
import com.litbig.spotify.ui.rememberSpotifyAppState
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import java.util.Locale

sealed class HomeSection(
    @StringRes val title: Int,
    val icons: Pair<ImageVector, ImageVector>,
    val route: String
) {
    data object Feed : HomeSection(
        title = R.string.home_feed,
        icons = Pair(Icons.Filled.Home, Icons.Outlined.Home),
        route = ROUTE_FEED
    )

    data object Search : HomeSection(
        title = R.string.home_search,
        icons = Pair(Icons.Filled.Search, Icons.Outlined.Search),
        route = ROUTE_SEARCH
    )

    data object Library : HomeSection(
        title = R.string.home_library,
        icons = Pair(Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic),
        route = ROUTE_LIBRARY
    )

    data object Premium : HomeSection(
        title = R.string.home_premium,
        icons = Pair(Icons.Filled.AcUnit, Icons.Outlined.AcUnit),
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
            ,
            navController = appState.navController,
            startDestination = HomeSection.Feed.route
        ) {
            addHomeGraph(
                modifier = Modifier
                    .consumeWindowInsets(padding),
                onTrackSelected = onTrackSelected
            )
        }

        PlayerBar(
            modifier = Modifier
                .padding(bottom = 85.dp)
                .padding(horizontal = 10.dp)
        )
    }
}

@Composable
fun SpotifyBottomBar(
    tabs: List<HomeSection>,
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
                            imageVector = section.icons.let { if (selected) it.first else it.second },
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