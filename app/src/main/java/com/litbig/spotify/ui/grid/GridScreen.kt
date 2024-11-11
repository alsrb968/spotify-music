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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.util.FileExtensions.getMusicMetadata
import timber.log.Timber
import java.io.File
import kotlin.random.Random

@Composable
fun GridScreen(
    modifier: Modifier = Modifier,
    musicFiles: List<File>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
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
        var displayedMusicFiles by remember { mutableStateOf(musicFiles.take(20)) }
        Timber.i("Displayed music files: ${displayedMusicFiles.size}")

        LazyRow(state = listState) {
            items(displayedMusicFiles.size) { index ->
                val file = displayedMusicFiles[index].getMusicMetadata()
                val dominantColor = getRandomPastelColor()

                GridCell(
                    albumArt = file.albumArt,
                    coreColor = dominantColor,
                    title = file.title,
                    artist = file.artist,
                    isPlayable = false,
                    onClick = {}
                )

                Spacer(modifier = Modifier.width(30.dp))
            }
        }

        LaunchedEffect(listState) {
            snapshotFlow { listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size }
                .collect { visibleItemCount ->
                    if (visibleItemCount >= displayedMusicFiles.size && displayedMusicFiles.size < musicFiles.size) {
                        val nextIndex = displayedMusicFiles.size
                        val newFiles = musicFiles.subList(
                            nextIndex,
                            (nextIndex + 20).coerceAtMost(musicFiles.size)
                        )
                        displayedMusicFiles = displayedMusicFiles + newFiles
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
    val saturation = 0.85f // Lower saturation for pastel tone
    val lightness = 0.6f // Higher lightness for a bright pastel color

    return Color.hsl(hue, saturation, lightness)
}

@DevicePreviews
@Composable
fun PreviewGridScreen() {
    GridScreen(
        musicFiles = emptyList()
    )
}