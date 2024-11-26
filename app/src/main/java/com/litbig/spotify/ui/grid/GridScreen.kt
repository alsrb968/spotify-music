package com.litbig.spotify.ui.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicInfoPagingData
import com.litbig.spotify.util.ColorExtractor.getRandomPastelColor
import kotlinx.coroutines.flow.Flow

@Composable
fun GridScreen(
    viewModel: GridViewModel = hiltViewModel(),
    navigateToList: (MusicInfo) -> Unit
) {
    GridScreen(
        navigateToList = navigateToList,
        favoritePagingFlow = viewModel.favoritesPagingFlow,
        albumsPagingFlow = viewModel.albumsPagingFlow,
        artistPagingFlow = viewModel.artistPagingFlow
    )
}

@Composable
fun GridScreen(
    modifier: Modifier = Modifier,
    favoritePagingFlow: Flow<PagingData<MusicInfo>>,
    albumsPagingFlow: Flow<PagingData<MusicInfo>>,
    artistPagingFlow: Flow<PagingData<MusicInfo>>,
    navigateToList: (MusicInfo) -> Unit
) {
    val bgColor = remember { getRandomPastelColor() }

    val favoritePagingItems = favoritePagingFlow.collectAsLazyPagingItems()
    val albumsPagingItems = albumsPagingFlow.collectAsLazyPagingItems()
    val artistPagingItems = artistPagingFlow.collectAsLazyPagingItems()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceDim),
        state = rememberLazyListState()
    ) {
        item(key = "favorite") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .gradientBackground(
                        ratio = 1f,
                        startColor = bgColor,
                        endColor = MaterialTheme.colorScheme.surfaceDim
                    )
            ) {
                GridMiniCategory(
                    title = "Your favorite tracks",
                    musicInfoPagingItems = favoritePagingItems,
                )

                Spacer(modifier = Modifier.height(26.dp))
            }
        }

        item(key = "albums") {
            GridCategory(
                navigateToList = navigateToList,
                title = "Your top albums",
                musicInfoPagingItems = albumsPagingItems
            )

            Spacer(modifier = Modifier.height(26.dp))
        }

        item(key = "artists") {
            GridCategory(
                shape = CircleShape,
                navigateToList = navigateToList,
                title = "Your top artists",
                musicInfoPagingItems = artistPagingItems
            )

            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}

fun Modifier.gradientBackground(
    ratio: Float = 0.3f,
    startColor: Color,
    endColor: Color
): Modifier = composed {
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    this
        .onSizeChanged { boxSize = it }
        .background(
            brush = Brush.linearGradient(
                colors = listOf(startColor, endColor),
                start = Offset(0f, 0f),
                end = Offset(0f, boxSize.height * ratio)
            )
        )
}

@DevicePreviews
@Composable
fun PreviewGridScreen() {
    SpotifyTheme {
        GridScreen(
            navigateToList = {},
            favoritePagingFlow = PreviewMusicInfoPagingData,
            albumsPagingFlow = PreviewMusicInfoPagingData,
            artistPagingFlow = PreviewMusicInfoPagingData
        )
    }
}