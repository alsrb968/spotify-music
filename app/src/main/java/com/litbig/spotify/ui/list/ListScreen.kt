package com.litbig.spotify.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.litbig.spotify.ui.grid.getRandomPastelColor
import com.litbig.spotify.ui.grid.gradientBackground
import com.litbig.spotify.ui.shared.FooterExpanded
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.util.FileExtensions.getMusicMetadata
import timber.log.Timber
import java.io.File

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    musicFiles: List<File>
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .gradientBackground(
                ratio = 0.5f,
                startColor = getRandomPastelColor(),
                endColor = Color.Transparent
            )
    ) {
        Column(
            modifier = Modifier
        ) {
            var displayedMusicFiles by remember { mutableStateOf(musicFiles.take(20)) }
            Timber.i("Displayed music files: ${displayedMusicFiles.size}")
            val metadataList = displayedMusicFiles.map { it.getMusicMetadata() }

            val listState = rememberLazyListState()

            LazyColumn(state = listState) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ListHeader(
                            modifier = Modifier,
                            metadataList = metadataList
                        )
                    }
                }
                item {
                    ListTitle()
                }
                items(metadataList.size) { index ->
                    val file = metadataList[index]
                    ListCell(
                        index = index + 1,
                        albumArt = file.albumArt,
                        title = file.title,
                        artist = file.artist,
                        album = file.album,
                        totalTime = file.formattedDuration,
                        onClick = { }
                    )
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
}

@DevicePreviews
@Composable
fun ListScreenPreview() {
    SpotifyTheme {
        ListScreen(
            musicFiles = emptyList()
        )
    }
}