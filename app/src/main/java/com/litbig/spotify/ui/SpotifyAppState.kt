package com.litbig.spotify.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.litbig.spotify.core.domain.model.MusicInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Grid : Screen("grid/{$ARG_CATEGORY}") {
        fun createRoute(category: String) = "grid/$category"
    }
    data object List : Screen("list/{$ARG_MUSIC_INFO}") {
        fun createRoute(musicInfo: String) = "list/$musicInfo"
    }

    companion object {
        const val ARG_CATEGORY = "category"
        const val ARG_MUSIC_INFO = "music_info"
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
    var isOnline by mutableStateOf(checkIfOnline())
        private set

    fun refreshOnline() {
        isOnline = checkIfOnline()
    }

    fun navigateToSplash(from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.Splash.route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
            }
        }
    }

    fun navigateToHome(from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) {
                    inclusive = true
                }
            }
        }
    }

    fun navigateToGrid(category: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            val encodedCategory = Uri.encode(category)
            navController.navigate(Screen.Grid.createRoute(encodedCategory))
        }
    }

    fun navigateToList(musicInfo: MusicInfo, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            val encodedMusicInfo = Uri.encode(Json.encodeToString(musicInfo))
            navController.navigate(Screen.List.createRoute(encodedMusicInfo))
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    @SuppressLint("ObsoleteSdkInt")
    @Suppress("DEPRECATION")
    private fun checkIfOnline(): Boolean {
        val cm = getSystemService(context, ConnectivityManager::class.java)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = cm?.getNetworkCapabilities(cm.activeNetwork) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            cm?.activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED