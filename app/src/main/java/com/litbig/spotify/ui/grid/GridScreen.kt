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
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicMetadataPagingData
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import kotlin.random.Random

@Composable
fun GridScreen(
    navigateToList: () -> Unit,
    viewModel: GridViewModel = hiltViewModel()
) {
    GridScreen(
        musicMetadataPagingItems = viewModel.musicMetadataPagingFlow
    )
}

@Composable
fun GridScreen(
    modifier: Modifier = Modifier,
    musicMetadataPagingItems: Flow<PagingData<MusicMetadata>>
) {
    val metadataPagingItems = musicMetadataPagingItems.collectAsLazyPagingItems()

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
                items(metadataPagingItems.itemCount) { index ->
                    val file = metadataPagingItems[index] ?: return@items
                    val dominantColor = getRandomPastelColor()

                    if (file.album.isNotEmpty()) {
                        Timber.d("Album: ${file.album}")
                    }

                    GridCell(
                        albumArt = file.albumArt?.asImageBitmap(),
                        coreColor = dominantColor,
                        title = file.title,
                        artist = file.artist,
                        album = file.album,
                        isPlayable = false,
                        onClick = {}
                    )

                    Spacer(modifier = Modifier.width(30.dp))
                }
            }
        }
    }
}

@Composable
fun extractDominantColor(imageBitmap: ImageBitmap): Color {
    var dominantColor by remember { mutableStateOf(Color.Transparent) }
    val bitmap = imageBitmap.asAndroidBitmap()

    Palette.from(bitmap).generate { palette ->
        palette?.let {
            dominantColor = Color(it.getDominantColor(Color.Transparent.toArgb()))
        }
    }

    return dominantColor
}

fun getRandomPastelColor(): Color {
    val hue = Random.nextFloat() * 360 // 0 to 360 for hue
    val saturation = 0.80f // Lower saturation for pastel tone
    val lightness = 0.6f // Higher lightness for a bright pastel color

    return Color.hsl(hue, saturation, lightness)
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
            musicMetadataPagingItems = PreviewMusicMetadataPagingData
        )
    }
}