package com.litbig.spotify.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.litbig.spotify.util.FileExtensions.getMusicMetadata
import timber.log.Timber
import java.io.File

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    musicFiles: List<File>
) {
    Box (
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier) {
            ListTitle()

            val listState = rememberLazyListState()
            var displayedMusicFiles by remember { mutableStateOf(musicFiles.take(20)) }
            Timber.i("Displayed music files: ${displayedMusicFiles.size}")

            LazyColumn(state = listState) {
                items(displayedMusicFiles.size) { index ->
                    val file = displayedMusicFiles[index].getMusicMetadata()
                    ListCell(
                        index = index + 1,
                        albumArt = file.albumArt,
                        title = file.title ?: "Unknown",
                        artist = file.artist ?: "Unknown",
                        album = file.album ?: "Unknown",
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

        musicFiles[0].getMusicMetadata().let { file ->
            FooterExpanded(
                modifier = Modifier
                    .align(Alignment.BottomStart),
                albumArt = file.albumArt,
                title = file.title ?: "Unknown",
                artist = file.artist ?: "Unknown",
                isFavorite = false,
                totalTime = file.formattedDuration,
                onClick = { }
            )
        }
    }
}