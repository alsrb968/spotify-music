package com.litbig.spotify.ui

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    data object Grid : Screen("grid")
    data object List : Screen("list/{$ARG_CATEGORY}") {
        fun createRoute(category: String) = "list/$category"
    }

    companion object {
        const val ARG_CATEGORY = "category"
    }
}

@Composable
fun rememberSpotifyAppState(
    navController: NavHostController = rememberNavController(),
    context : Context = LocalContext.current
) = remember(navController, context) {
    SpotifyAppState(navController, context)
}

class SpotifyAppState(
    val navController: NavHostController,
    private val context: Context
) {
    fun navigateToList(albumName: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            val name = Uri.encode(albumName)
            navController.navigate(Screen.List.createRoute(name))
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED