package com.litbig.spotify.ui.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.ui.grid.gradientBackground
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicMetadataPagingData
import com.litbig.spotify.util.ColorExtractor.extractDominantColor
import com.litbig.spotify.util.ConvertExtensions.toHumanReadableDuration
import kotlinx.coroutines.flow.Flow

@Composable
fun ListScreen(
    viewModel: ListViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    BackHandler { navigateBack() }
    ListScreen(
        metadataPagingFlow = viewModel.metadataPagingFlow,
        navigateBack = navigateBack
    )
}

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    metadataPagingFlow: Flow<PagingData<MusicMetadata>>,
    navigateBack: () -> Unit
) {
    val metadataPagingItems = metadataPagingFlow.collectAsLazyPagingItems()
    val albumArtFirst =
        metadataPagingItems.itemSnapshotList.items.firstOrNull()?.albumArt?.asImageBitmap()

    val listState = rememberLazyListState()

    LazyColumn(state = listState) {

        item {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .gradientBackground(
                        ratio = 1f,
                        startColor = albumArtFirst?.let { extractDominantColor(it) }
                            ?: Color.Transparent,
                        endColor = Color.Transparent
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp)
                        .clickable { navigateBack() },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ListHeader(
                        modifier = Modifier,
                        metadataList = metadataPagingItems.itemSnapshotList.items
                    )
                }

                ListTitle()

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        items(metadataPagingItems.itemCount) { index ->
            val file = metadataPagingItems[index] ?: return@items
            ListCell(
                index = index + 1,
                isPlaying = index == 0,
                albumArt = file.albumArt?.asImageBitmap(),
                title = file.title,
                artist = file.artist,
                album = file.album,
                totalTime = file.duration.toHumanReadableDuration(),
                onClick = { }
            )
        }
    }
}

@DevicePreviews
@Composable
fun ListScreenPreview() {
    SpotifyTheme {
        ListScreen(
            metadataPagingFlow = PreviewMusicMetadataPagingData,
            navigateBack = {}
        )
    }
}