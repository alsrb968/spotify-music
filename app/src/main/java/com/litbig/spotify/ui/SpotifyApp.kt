@file:OptIn(
    ExperimentalSharedTransitionApi::class
)

package com.litbig.spotify.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.litbig.spotify.ui.grid.GridScreen
import com.litbig.spotify.ui.home.HomeScreen
import com.litbig.spotify.ui.list.ListScreen
import com.litbig.spotify.ui.player.PlayerBar
import com.litbig.spotify.ui.player.PlayerScreen
import com.litbig.spotify.ui.splash.SplashScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotifyApp(
    appState: SpotifyAppState = rememberSpotifyAppState()
) {
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this,
        ) {
            val sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.PartiallyExpanded
            )
            val scope = rememberCoroutineScope()
            var playerBarHeight by remember { mutableIntStateOf(0) }
            BottomSheetScaffold(
                scaffoldState = rememberBottomSheetScaffoldState(
                    bottomSheetState = sheetState
                ),
                sheetContent = {
                    when (sheetState.currentValue) {
                        SheetValue.PartiallyExpanded -> {
                            PlayerBar(
                                modifier = Modifier.onSizeChanged { size ->
                                    playerBarHeight = size.height
                                },
                                navigateToPlayer = {
                                    scope.launch {
                                        sheetState.expand()
                                    }
                                }
                            )
                        }

                        SheetValue.Expanded -> {
                            PlayerScreen(
                                onCollapse = {
                                    scope.launch {
                                        sheetState.partialExpand()
                                    }
                                }
                            )
                        }

                        else -> {}
                    }
                },
                sheetMaxWidth = LocalConfiguration.current.screenWidthDp.dp,
                sheetDragHandle = null,
                sheetPeekHeight = with(LocalDensity.current) {
                    (playerBarHeight * 0.99f).toInt().toDp()
                },
                sheetShape = RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),
                sheetContainerColor = Color.Transparent,
            ) {

                if (appState.isOnline) {
                    NavHost(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background),
                        navController = appState.navController,
                        startDestination = Screen.Splash.route
                    ) {
                        composableWithCompositionLocal(Screen.Splash.route) { backStackEntry ->
                            SplashScreen(
                                navigateToHome = {
                                    appState.navigateToHome(backStackEntry)
                                }
                            )
                        }

                        composableWithCompositionLocal(Screen.Home.route) { backStackEntry ->
                            HomeScreen(
                                navigateToGrid = { category ->
                                    appState.navigateToGrid(category, backStackEntry)
                                },
                                navigateToList = { musicInfo ->
                                    appState.navigateToList(musicInfo, backStackEntry)
                                }
                            )
                        }

                        composableWithCompositionLocal(Screen.Grid.route) { backStackEntry ->
                            GridScreen(
                                navigateToList = { musicInfo ->
                                    appState.navigateToList(musicInfo, backStackEntry)
                                },
                                navigateBack = appState::navigateBack
                            )
                        }

                        composableWithCompositionLocal(Screen.List.route) {
                            ListScreen(
                                navigateBack = appState::navigateBack
                            )
                        }
                    }
                } else {
                    OfflineDialog { appState.refreshOnline() }
                }

            }
        }
    }
}

@Composable
fun OfflineDialog(onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "인터넷 연결 오류") },
        text = { Text(text = "음악 정보 리스트를 가져올 수 없습니다.\n인터넷 연결을 확인 후 다시 시도해주세요.") },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(text = "재시도")
            }
        }
    )
}

fun NavGraphBuilder.composableWithCompositionLocal(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? = {
        fadeIn(nonSpatialExpressiveSpring())
    },
    exitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? = {
        fadeOut(nonSpatialExpressiveSpring())
    },
    popEnterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? =
        enterTransition,
    popExitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? =
        exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route,
        arguments,
        deepLinks,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition
    ) {
        CompositionLocalProvider(
            LocalNavAnimatedVisibilityScope provides this@composable
        ) {
            content(it)
        }
    }
}

fun <T> spatialExpressiveSpring() = spring<T>(
    dampingRatio = 0.8f,
    stiffness = 380f
)

fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f
)

val imageBoundsTransform = BoundsTransform { _, _ ->
    spatialExpressiveSpring()
}

val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }