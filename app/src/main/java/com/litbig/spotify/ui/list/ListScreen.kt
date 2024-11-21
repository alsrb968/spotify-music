package com.litbig.spotify.ui.list

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.ui.grid.gradientBackground
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicInfo
import com.litbig.spotify.ui.tooling.PreviewMusicMetadataPagingData
import com.litbig.spotify.util.ColorExtractor.extractDominantColor
import com.litbig.spotify.util.ConvertExtensions.toHumanReadableDuration
import kotlinx.coroutines.flow.Flow

@Composable
fun ListScreen(
    viewModel: ListViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    ListScreen(
        musicInfo = viewModel.musicInfo,
        metadataPagingFlow = viewModel.metadataPagingFlow,
        navigateBack = navigateBack
    )
}

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    musicInfo: MusicInfo,
    metadataPagingFlow: Flow<PagingData<MusicMetadata>>,
    navigateBack: () -> Unit
) {
    val metadataPagingItems = metadataPagingFlow.collectAsLazyPagingItems()
    val albumArt = loadImageBitmapFromUrl(musicInfo.imageUrl)

    val listState = rememberLazyListState()

    LazyColumn(state = listState) {

        item {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .gradientBackground(
                        ratio = 1f,
                        startColor = albumArt?.let { extractDominantColor(it) }
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
                        musicInfo = musicInfo,
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
                imageUrl = file.albumArtUrl,
                title = file.title,
                artist = file.artist,
                album = file.album,
                totalTime = file.duration.toHumanReadableDuration(),
                onClick = { }
            )
        }
    }
}

@Composable
fun loadImageBitmapFromUrl(imageUrl: String?): ImageBitmap? {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageUrl) {
        if (imageUrl != null) {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .build()

            val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
            if (result is BitmapDrawable) {
                imageBitmap = result.bitmap.asImageBitmap()
            }
        }
    }

    return imageBitmap
}

@DevicePreviews
@Composable
fun ListScreenPreview() {
    SpotifyTheme {
        ListScreen(
            musicInfo = PreviewMusicInfo,
            metadataPagingFlow = PreviewMusicMetadataPagingData,
            navigateBack = {}
        )
    }
}