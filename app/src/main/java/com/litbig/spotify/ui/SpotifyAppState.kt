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
import com.litbig.spotify.core.domain.model.MusicInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

sealed class Screen(val route: String) {
    data object Grid : Screen("grid")
    data object List : Screen("list/{$ARG_MUSIC_INFO}") {
        fun createRoute(musicInfo: String) = "list/$musicInfo"
    }

    companion object {
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
    fun navigateToList(musicInfo: MusicInfo, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            val serializedMusicInfo = Uri.encode(Json.encodeToString(musicInfo))
            Timber.w("serializedMusicInfo: $serializedMusicInfo")
            navController.navigate(Screen.List.createRoute(serializedMusicInfo))
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED