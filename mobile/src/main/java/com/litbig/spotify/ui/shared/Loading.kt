package com.litbig.spotify.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews

@Composable
fun Loading(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@DevicePreviews
@Composable
fun LoadingPreview() {
    SpotifyTheme {
        Loading()
    }
}