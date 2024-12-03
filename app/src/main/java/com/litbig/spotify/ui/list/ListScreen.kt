@file:OptIn(ExperimentalMaterial3Api::class)

package com.litbig.spotify.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicInfo
import com.litbig.spotify.ui.tooling.PreviewMusicMetadataPagingData
import com.litbig.spotify.util.ConvertExtensions.toHumanReadableDuration
import com.litbig.spotify.util.extractDominantColorFromUrl
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
        onTrack = viewModel::play,
        onTracks = viewModel::play,
        onTrackFavorite = viewModel::toggleFavoriteTrack,
        isTrackFavorite = viewModel::isFavoriteTrack,
        onAddPlaylist = viewModel::addPlaylist
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
    onTrack: (MusicMetadata) -> Unit,
    onTracks: (List<MusicMetadata>) -> Unit,
    onTrackFavorite: (String, String?) -> Unit,
    isTrackFavorite: (String) -> Flow<Boolean>,
    onAddPlaylist: (List<MusicMetadata>) -> Unit
) {
    val metadataPagingItems = metadataPagingFlow.collectAsLazyPagingItems()

    val context = LocalContext.current // Compose에서 Context 가져오기
    var dominantColor by remember { mutableStateOf(Color.Transparent) }

    // LaunchedEffect로 비동기 작업 수행
    LaunchedEffect(musicInfo.imageUrl) {
        dominantColor = extractDominantColorFromUrl(context, musicInfo.imageUrl)
    }

    val isLoading = metadataPagingItems.loadState.refresh is LoadState.Loading

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LazyColumn(state = rememberLazyListState()) {

        item {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .gradientBackground(
                        ratio = 1f,
                        startColor = dominantColor,
                        endColor = MaterialTheme.colorScheme.background
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
                    onPlay = {
                        val items = metadataPagingItems.itemSnapshotList.items
                        onTracks(items)
                    },
                    onFavorite = onFavorite,
                    isFavorite = isFavorite,
                    onMore = {
                        showBottomSheet = true
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (isLoading) {
            items(3) {
                SkeletonListCell()
            }
        } else {
            items(metadataPagingItems.itemCount) { index ->
                val metadata = metadataPagingItems[index] ?: return@items
                val isFav = isTrackFavorite(metadata.title).collectAsState(initial = false).value
                ListCell(
                    index = index + 1,
                    isPlaying = index == 0,
                    imageUrl = metadata.albumArtUrl,
                    title = metadata.title,
                    artist = metadata.artist,
                    album = metadata.album,
                    isFavorite = isFav,
                    totalTime = metadata.duration.toHumanReadableDuration(),
                    onClick = { onTrack(metadata) },
                    onFavorite = { onTrackFavorite(metadata.title, metadata.albumArtUrl) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showBottomSheet) {
        OptionBottomSheet(
            sheetState = sheetState,
            onShow = { showBottomSheet = it },
            title = musicInfo.title,
            subTitle = musicInfo.content,
            optionActions = OptionActions(
                onPlaylistPlay = {
                    val items = metadataPagingItems.itemSnapshotList.items
                    onAddPlaylist(items)
                },
                onPlaylistAdd = {},
                onShare = {}
            )
        )
    }
}

@Composable
fun OptionBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onShow: (Boolean) -> Unit,
    title: String = "",
    subTitle: String = "",
    optionActions: OptionActions
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = {
            onShow(false)
        },
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = true,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 60.dp),
                text = title,
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                modifier = Modifier
                    .padding(start = 60.dp),
                text = subTitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                color = MaterialTheme.colorScheme.outline,
                thickness = 1.dp,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                CardButton(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
                    text = "다음에 재생",
                    onClick = {
                        onShow(false)
                        optionActions.onPlaylistPlay()
                    }
                )

                CardButton(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                    text = "재생목록에 저장",
                    onClick = {
                        onShow(false)
                        optionActions.onPlaylistAdd()
                    }
                )

                CardButton(
                    imageVector = Icons.Default.Share,
                    text = "공유",
                    onClick = {
                        onShow(false)
                        optionActions.onShare()
                    }
                )
            }
        }


    }
}

data class OptionActions(
    val onPlaylistPlay: () -> Unit,
    val onPlaylistAdd: () -> Unit,
    val onShare: () -> Unit
)

@Composable
fun CardButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    text: String = "",
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(width = 120.dp, height = 90.dp)
                .clickable {
                    onClick()
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    imageVector = imageVector,
                    contentDescription = text,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (text.isNotEmpty()) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
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
            onTracks = {},
            onTrackFavorite = { _, _ -> },
            isTrackFavorite = { flowOf(false) },
            onAddPlaylist = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@DevicePreviews
@Composable
fun OptionBottomSheetPreview() {
    SpotifyTheme {
        OptionBottomSheet(
            sheetState = rememberStandardBottomSheetState(),
            onShow = {},
            title = PreviewMusicInfo.title,
            subTitle = PreviewMusicInfo.content,
            optionActions = OptionActions(
                onPlaylistPlay = {},
                onPlaylistAdd = {},
                onShare = {}
            )
        )
    }
}