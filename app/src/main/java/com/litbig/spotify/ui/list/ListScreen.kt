package com.litbig.spotify.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.ui.grid.gradientBackground
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicInfo
import com.litbig.spotify.ui.tooling.PreviewMusicMetadataPagingData
import com.litbig.spotify.util.ColorExtractor.extractDominantColorFromUrl
import com.litbig.spotify.util.ConvertExtensions.toHumanReadableDuration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun ListScreen(
    viewModel: ListViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val onFavorite = {
        when (viewModel.musicInfo.category) {
            "album" ->
                viewModel.toggleFavoriteAlbum(
                    viewModel.musicInfo.title,
                    viewModel.musicInfo.imageUrl
                )

            "artist" ->
                viewModel.toggleFavoriteArtist(
                    viewModel.musicInfo.title,
                    viewModel.musicInfo.imageUrl
                )

            else -> viewModel.toggleFavoriteTrack(
                viewModel.musicInfo.title,
                viewModel.musicInfo.imageUrl
            )
        }
    }

    val isFavorite = when (viewModel.musicInfo.category) {
        "album" -> viewModel.isFavoriteAlbum(viewModel.musicInfo.title)
        "artist" -> viewModel.isFavoriteArtist(viewModel.musicInfo.title)
        else -> viewModel.isFavoriteTrack(viewModel.musicInfo.title)
    }

    ListScreen(
        musicInfo = viewModel.musicInfo,
        metadataPagingFlow = viewModel.metadataPagingFlow,
        navigateBack = navigateBack,
        onFavorite = onFavorite,
        isFavorite = isFavorite,
        onTrack = { },
        onTrackFavorite = viewModel::toggleFavoriteTrack,
        isTrackFavorite = viewModel::isFavoriteTrack
    )
}

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    musicInfo: MusicInfo,
    metadataPagingFlow: Flow<PagingData<MusicMetadata>>,
    navigateBack: () -> Unit,
    onFavorite: () -> Unit,
    isFavorite: Flow<Boolean>,
    onTrack: () -> Unit,
    onTrackFavorite: (String, String?) -> Unit,
    isTrackFavorite: (String) -> Flow<Boolean>
) {
    val metadataPagingItems = metadataPagingFlow.collectAsLazyPagingItems()

    val context = LocalContext.current // Compose에서 Context 가져오기
    var dominantColor by remember { mutableStateOf(Color.Transparent) }

    // LaunchedEffect로 비동기 작업 수행
    LaunchedEffect(musicInfo.imageUrl) {
        dominantColor = extractDominantColorFromUrl(context, musicInfo.imageUrl)
    }

    val listState = rememberLazyListState()

    LazyColumn(state = listState) {

        item {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .gradientBackground(
                        ratio = 1f,
                        startColor = dominantColor,
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

                ListTitle(
                    onFavorite = onFavorite,
                    isFavorite = isFavorite
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        items(metadataPagingItems.itemCount) { index ->
            val file = metadataPagingItems[index] ?: return@items
            val isFav = isTrackFavorite(file.title).collectAsState(initial = false).value
            ListCell(
                index = index + 1,
                isPlaying = index == 0,
                imageUrl = file.albumArtUrl,
                title = file.title,
                artist = file.artist,
                album = file.album,
                isFavorite = isFav,
                totalTime = file.duration.toHumanReadableDuration(),
                onClick = { },
                onFavorite = { onTrackFavorite(file.title, file.albumArtUrl) }
            )
        }
    }
}

@DevicePreviews
@Composable
fun ListScreenPreview() {
    SpotifyTheme {
        ListScreen(
            musicInfo = PreviewMusicInfo,
            metadataPagingFlow = PreviewMusicMetadataPagingData,
            navigateBack = {},
            onFavorite = {},
            isFavorite = flowOf(false),
            onTrack = {},
            onTrackFavorite = { _, _ -> },
            isTrackFavorite = { flowOf(false) }
        )
    }
}