@file:OptIn(
    ExperimentalFoundationApi::class
)

package com.litbig.spotify.ui.home.album

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.core.design.extension.extractDominantColorFromUrl
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.ui.components.*
import com.litbig.spotify.ui.models.AlbumUiModel
import com.litbig.spotify.ui.models.TrackUiModel
import com.litbig.spotify.ui.shared.Loading
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewAlbumUiModel
import com.litbig.spotify.ui.tooling.PreviewTrackUiModels
import kotlinx.coroutines.flow.collectLatest
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AlbumDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AlbumDetailUiEffect.ShowToast -> onShowSnackBar(effect.message)
            }
        }
    }

    when (val s = state) {
        is AlbumDetailUiState.Loading -> {
            Loading(modifier = modifier.fillMaxWidth())
        }

        is AlbumDetailUiState.Ready -> {

            val context = LocalContext.current
            LaunchedEffect(Unit) {
                val dominantColor = extractDominantColorFromUrl(context, s.album.imageUrl)
                viewModel.sendIntent(AlbumDetailUiIntent.SetDominantColor(dominantColor))
            }

            AlbumDetailScreen(
                modifier = modifier,
                album = s.album,
                tracks = s.tracks,
                playingTrackId = s.playingTrackId,
                onPlayTrack = { trackId ->
                    viewModel.sendIntent(AlbumDetailUiIntent.PlayTrack(trackId))
                },
                onPlayTracks = { trackIds ->
                    viewModel.sendIntent(AlbumDetailUiIntent.PlayTracks(trackIds))
                },
                onAddTrack = { trackId ->
                    viewModel.sendIntent(AlbumDetailUiIntent.AddTrack(trackId))
                },
                onAddTracks = { trackIds ->
                    viewModel.sendIntent(AlbumDetailUiIntent.AddTracks(trackIds))
                },
                onToggleFavoriteAlbum = { albumId ->
                    viewModel.sendIntent(AlbumDetailUiIntent.ToggleFavoriteAlbum(albumId))
                },
                onToggleFavoriteTrack = { trackId ->
                    viewModel.sendIntent(AlbumDetailUiIntent.ToggleFavoriteTrack(trackId))
                },
                navigateBack = navigateBack
            )
        }
    }
}

@Composable
fun AlbumDetailScreen(
    modifier: Modifier = Modifier,
    album: AlbumUiModel,
    tracks: List<TrackUiModel>?,
    playingTrackId: String?,
    onPlayTrack: (String) -> Unit,
    onPlayTracks: (List<String>) -> Unit,
    onAddTrack: (String) -> Unit,
    onAddTracks: (List<String>) -> Unit,
    onToggleFavoriteAlbum: (String) -> Unit,
    onToggleFavoriteTrack: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    val listState = rememberLazyListState()
    val scrollProgress by remember {
        derivedStateOf {
            val maxOffset = 600f // 희미해지기 시작하는 최대 오프셋 값
            val firstVisibleItem = listState.firstVisibleItemIndex
            val scrollOffset = listState.firstVisibleItemScrollOffset.toFloat()
            if (firstVisibleItem == 0) {
                1f - (scrollOffset / maxOffset).coerceIn(0f, 1f)
            } else 0f
        }
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        CollapsedTopBar(
            modifier = Modifier.zIndex(2f),
            albumName = album.name,
            dominantColor = album.dominantColor,
            progress = 1f - scrollProgress
        )
        ExpandedTopBar(
            imageUrl = album.imageUrl,
            dominantColor = album.dominantColor,
            scrollProgress = scrollProgress
        )

        IconButton(
            modifier = Modifier
                .zIndex(3f)
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 32.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            onClick = navigateBack,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Spacer(modifier = Modifier.height(EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT * 2))
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(COLLAPSED_TOP_BAR_HEIGHT)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = album.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            item {
                AlbumInfoTitle(
                    modifier = Modifier
                        .gradientBackground(
                            ratio = 1f,
                            startColor = album.dominantColor,
                            endColor = MaterialTheme.colorScheme.background
                        ),
                    artists = album.artists,
                    tracksTotalTime = album.totalTime,
                    onPlayTracks = {
                        tracks?.map { it.id }?.let {
                            onPlayTracks(it)
                        }
                    }
                )
            }

            tracks?.let {
                items(it.size) { index ->
                    val track = it[index]
                    TrackItem(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background),
                        imageUrl = track.imageUrl,
                        isPlaying = playingTrackId == track.id,
                        title = track.name,
                        artist = track.artists,
                        onClick = { /* todo */ },
                        onMore = { /* todo */ }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }
}

@Composable
fun AlbumInfoTitle(
    modifier: Modifier = Modifier,
    artists: String,
    tracksTotalTime: Long,
    onPlayTracks: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = artists,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            IconButtonWithText(
                icon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                text = "나만의 플레이리스트",
                onClick = {/* todo */ }
            )

            IconButtonWithText(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = "추천 상세정보",
                onClick = {/* todo */ }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val duration = tracksTotalTime.milliseconds
        val time = if (duration.inWholeHours > 0) {
            "%s시간 %s분".format(duration.inWholeHours, duration.inWholeMinutes % 60)
        } else {
            "%s분".format(duration.inWholeMinutes)
        }
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* todo */ }
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddCircleOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { /* todo */ }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowCircleDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { /* todo */ }
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            FloatingActionButton(
                modifier = Modifier,
                shape = CircleShape,
                onClick = onPlayTracks,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    modifier = Modifier
                        .size(36.dp),
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun IconButtonWithText(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickableScaled { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier,
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@DevicePreviews
@Composable
private fun AlbumDetailScreenPreview() {
    SpotifyTheme {
        AlbumDetailScreen(
            album = PreviewAlbumUiModel,
            tracks = PreviewTrackUiModels,
            playingTrackId = null,
            onPlayTrack = {},
            onPlayTracks = {},
            onAddTrack = {},
            onAddTracks = {},
            onToggleFavoriteAlbum = {},
            onToggleFavoriteTrack = {},
            navigateBack = {}
        )
    }
}