package com.litbig.spotify.ui.shared

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.litbig.spotify.ui.LocalNavAnimatedVisibilityScope
import com.litbig.spotify.ui.LocalSharedTransitionScope
import com.litbig.spotify.ui.imageBoundsTransform

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun getSharedModifier(
    key: String
): Modifier {
    val isPreview = LocalInspectionMode.current
    val sharedModifier = if (!isPreview) {
        val sharedTransitionScope = LocalSharedTransitionScope.current
            ?: throw IllegalStateException("No Scope found")
        val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
            ?: throw IllegalStateException("No animatedVisibilityScope found")
        with(sharedTransitionScope) {
            Modifier
                .sharedBounds(
                sharedContentState = rememberSharedContentState(key),
                animatedVisibilityScope = animatedVisibilityScope,
                enter = fadeIn(),
                exit = fadeOut(),
                boundsTransform = imageBoundsTransform
            )
        }
    } else Modifier

    return sharedModifier
}