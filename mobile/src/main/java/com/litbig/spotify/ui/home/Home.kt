package com.litbig.spotify.ui.home

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.litbig.spotify.R
import com.litbig.spotify.ui.home.feed.FeedScreen
import com.litbig.spotify.ui.home.search.SearchScreen
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import java.util.Locale

fun NavGraphBuilder.addHomeGraph(
    modifier: Modifier = Modifier,
    onTrackSelected: (String, NavBackStackEntry) -> Unit
) {
    composable(HomeSections.FEED.route) { from ->
        FeedScreen(
            modifier = modifier,
            onTrackClick = { trackId -> onTrackSelected(trackId, from) }
        )
    }

    composable(HomeSections.SEARCH.route) { from ->
        SearchScreen(
            modifier = modifier,
            onTrackClick = { trackId -> onTrackSelected(trackId, from) }
        )
    }

    composable(HomeSections.LIBRARY.route) { from ->

    }

    composable(HomeSections.PREMIUM.route) { from ->

    }
}

enum class HomeSections(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    FEED(R.string.home_feed, Icons.Outlined.Home, "home/feed"),
    SEARCH(R.string.home_search, Icons.Outlined.Search, "home/search"),
    LIBRARY(R.string.home_library, Icons.Outlined.LibraryMusic, "home/library"),
    PREMIUM(R.string.home_premium, Icons.Outlined.AcUnit, "home/premium")
}

@Composable
fun SpotifyBottomBar(
    tabs: List<HomeSections>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
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
            tabs = HomeSections.entries,
            currentRoute = HomeSections.FEED.route,
            navigateToRoute = { }
        )
    }
}