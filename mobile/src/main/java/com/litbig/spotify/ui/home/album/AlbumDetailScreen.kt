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
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.core.design.extension.extractDominantColorFromUrl
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.ui.components.TrackItem
import com.litbig.spotify.ui.shared.Loading
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewAlbumDetailUiState
import kotlin.time.Duration.Companion.milliseconds

val COLLAPSED_TOP_BAR_HEIGHT = 90.dp
val EXPANDED_TOP_BAR_HEIGHT = 400.dp

@Composable
fun AlbumDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is AlbumDetailUiState.Loading -> {
            Loading(modifier = modifier.fillMaxWidth())
        }

        is AlbumDetailUiState.Ready -> {

            val context = LocalContext.current
            LaunchedEffect(Unit) {
                val dominantColor = extractDominantColorFromUrl(context, s.imageUrl)
                viewModel.setDominantColor(dominantColor)
            }

            AlbumDetailScreen(
                modifier = modifier,
                uiState = s,
                onPlayTracks = viewModel::play,
                navigateToBack = navigateToBack
            )
        }
    }
}

@Composable
fun AlbumDetailScreen(
    modifier: Modifier = Modifier,
    uiState: AlbumDetailUiState.Ready,
    onPlayTracks: (List<String>) -> Unit,
    navigateToBack: () -> Unit,
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
            albumName = uiState.albumName,
            dominantColor = uiState.dominantColor,
            progress = 1f - scrollProgress
        )
        ExpandedTopBar(
            imageUrl = uiState.imageUrl,
            dominantColor = uiState.dominantColor,
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
            onClick = navigateToBack,
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
                        text = uiState.albumName,
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
                            startColor = uiState.dominantColor,
                            endColor = MaterialTheme.colorScheme.background
                        ),
                    artists = uiState.artistNames,
                    tracksTotalTime = uiState.totalTime,
                    onPlayTracks = {
                        uiState.trackInfos?.map { it.id }?.let {
                            onPlayTracks(it)
                        }
                    }
                )
            }

            uiState.trackInfos?.let {
                items(it.size) { index ->
                    val track = it[index]
                    TrackItem(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background),
                        imageUrl = track.imageUrl,
                        isPlaying = uiState.playingTrackId == track.id,
                        title = track.title,
                        artist = track.artist,
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
private fun ExpandedTopBar(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    dominantColor: Color = MaterialTheme.colorScheme.background,
    scrollProgress: Float = 0f
) {
    Box(
        modifier = modifier
            .background(dominantColor)
            .fillMaxWidth()
            .height(EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT),
        contentAlignment = Alignment.BottomStart
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .alpha(scrollProgress)
                .scale(1.0f + scrollProgress * 0.1f),
            model = imageUrl,
            contentDescription = "Album Art",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            placeholder = rememberVectorPainter(image = Icons.Default.Album),
            error = rememberVectorPainter(image = Icons.Default.Error)
        )
    }
}

@DevicePreviews
@Composable
fun PreviewExpandedTopBar() {
    SpotifyTheme {
        ExpandedTopBar(
            imageUrl = "https://i.s",
        )
    }
}

@Composable
private fun CollapsedTopBar(
    modifier: Modifier = Modifier,
    albumName: String,
    dominantColor: Color = MaterialTheme.colorScheme.background,
    progress: Float
) {
    Box(
        modifier = modifier
            .background(dominantColor.copy(alpha = progress))
            .fillMaxWidth()
            .height(COLLAPSED_TOP_BAR_HEIGHT)
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        // 텍스트 애니메이션
        Text(
            modifier = Modifier
                .padding(start = 60.dp, bottom = 4.dp)
                .graphicsLayer {
                    alpha = progress.coerceIn(0f, 1f)
                    translationY = (1f - progress) * 20f // 위로 이동
                },
            text = albumName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@DevicePreviews
@Composable
fun PreviewCollapsedTopBar() {
    SpotifyTheme {
        Column {
            CollapsedTopBar(
                albumName = "Album Name",
                progress = 1f
            )
            CollapsedTopBar(
                albumName = "Album Name",
                progress = 0f
            )
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
fun PreviewAlbumDetailScreen() {
    SpotifyTheme {
        AlbumDetailScreen(
            uiState = PreviewAlbumDetailUiState,
            onPlayTracks = {},
            navigateToBack = {}
        )
    }
}