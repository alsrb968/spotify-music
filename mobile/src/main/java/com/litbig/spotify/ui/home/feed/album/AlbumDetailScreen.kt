@file:OptIn(ExperimentalLayoutApi::class)

package com.litbig.spotify.ui.home.feed.album

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.component.shimmerPainter
import com.litbig.spotify.ui.components.TrackItem
import com.litbig.spotify.ui.shared.Loading
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewAlbumDetailUiState
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AlbumDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        AlbumDetailUiState.Loading -> {
            Loading(modifier = modifier)
        }

        is AlbumDetailUiState.Ready -> {
            AlbumDetailScreen(
                modifier = modifier,
                uiState = s,
                onPlayTracks = viewModel::play,
                navigateToBack = navigateToBack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    modifier: Modifier = Modifier,
    uiState: AlbumDetailUiState.Ready,
    onPlayTracks: (List<String>) -> Unit,
    navigateToBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        TrackList(
            modifier = Modifier,
            uiState = uiState,
            onPlayTracks = onPlayTracks,
        )

        IconButton(
            onClick = navigateToBack,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackList(
    modifier: Modifier = Modifier,
    uiState: AlbumDetailUiState.Ready,
    onPlayTracks: (List<String>) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = rememberLazyListState(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
//        collapsedFraction = scrollBehavior.state.collapsedFraction
        item {
            AsyncImage(
                modifier = Modifier
                    .padding(16.dp)
//                    .scale(1f - collapsedFraction/4)
//                    .graphicsLayer(
//                        translationY = imageOffset.toFloat(),
//                        alpha = imageAlpha
//                    )
                ,
                model = uiState.imageUrl,
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                placeholder = rememberVectorPainter(image = Icons.Default.Album),
                error = rememberVectorPainter(image = Icons.Default.Error)
            )
        }

        item {
            AlbumInfoTitle(
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
                    imageUrl = track.imageUrl,
                    title = track.title,
                    artist = track.artist,
                    onClick = { /* todo */ },
                    onMore = { /* todo */ }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(200.dp))
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
            .fillMaxWidth(),
    ) {
        Text(
            text = artists,
        )

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
//                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = "추천 상세정보",
                onClick = {/* todo */ }
            )
        }

        val duration = tracksTotalTime.milliseconds
        val time = if (duration.inWholeHours > 0) {
            "%s시간 %s분".format(duration.inWholeHours, duration.inWholeMinutes % 60)
        } else {
            "%s분".format(duration.inWholeMinutes)
        }
        Text(
            text = time
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
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = { /* todo */ }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowCircleDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = { /* todo */ }
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            FloatingActionButton(
                modifier = Modifier
                    .padding(8.dp),
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
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.padding(8.dp),
        ) {
            icon()
        }
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = text,
            style = MaterialTheme.typography.bodySmall,
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