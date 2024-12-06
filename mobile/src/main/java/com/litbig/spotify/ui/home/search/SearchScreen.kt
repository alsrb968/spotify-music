package com.litbig.spotify.ui.home.search

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onTrackClick: (String) -> Unit
) {
    Text("Search")
}