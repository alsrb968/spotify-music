package com.litbig.spotify.ui.home.feed.album

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.R
import com.litbig.spotify.ui.components.TrackItem
import com.litbig.spotify.ui.shared.Loading
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewAlbumDetailUiState
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
    navigateToBack: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = uiState.albumName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { /* todo */ }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { /* todo */ }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        TrackList(
            modifier = Modifier.padding(padding),
            uiState = uiState
        )
    }
}

@Composable
fun TrackList(
    modifier: Modifier = Modifier,
    uiState: AlbumDetailUiState.Ready,
) {
    LazyColumn(
        modifier = modifier,
        state = rememberLazyListState(),
    ) {
        item {
            AlbumInfoTitle(
                artists = uiState.artistNames,
                tracksTotalTime = uiState.totalTime,
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
    }
}

@Composable
fun AlbumInfoTitle(
    modifier: Modifier = Modifier,
    artists: String,
    tracksTotalTime: Long,

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
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
//            modifier = Modifier.size(24.dp),
            onClick = onClick
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
            navigateToBack = {}
        )
    }
}