package com.litbig.spotify.ui.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.palette.graphics.Palette
import com.litbig.spotify.core.domain.model.Album
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewAlbumPagingData
import com.litbig.spotify.util.ColorExtractor.getRandomPastelColor
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

@Composable
fun GridScreen(
    viewModel: GridViewModel = hiltViewModel(),
    navigateToList: (Album) -> Unit
) {
    GridScreen(
        navigateToList = navigateToList,
        musicAlbumsPagingFlow = viewModel.musicAlbumsPagingFlow
    )
}

@Composable
fun GridScreen(
    modifier: Modifier = Modifier,
    musicAlbumsPagingFlow: Flow<PagingData<Album>>,
    navigateToList: (Album) -> Unit
) {
    val albumsPagingItems = musicAlbumsPagingFlow.collectAsLazyPagingItems()

    Box(
        modifier = modifier
            .fillMaxSize()
            .gradientBackground(
                startColor = getRandomPastelColor(),
                endColor = Color.Transparent
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Your top mixes",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    text = "SEE ALL",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            val listState = rememberLazyListState()

            LazyRow(state = listState) {
                items(albumsPagingItems.itemCount) { index ->
                    val album = albumsPagingItems[index]
                    val dominantColor = getRandomPastelColor()

                    GridCell(
                        albumArt = album?.albumArt?.asImageBitmap(),
                        coreColor = dominantColor,
                        title = album?.name ?: "",
                        artist = album?.artist ?: "",
                        album = album?.name ?: "",
                        isPlayable = false,
                        onClick = { album?.let { navigateToList(it) } }
                    )

                    Spacer(modifier = Modifier.width(30.dp))
                }
            }
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
            musicAlbumsPagingFlow = PreviewAlbumPagingData
        )
    }
}