@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

package com.litbig.spotify.ui.home.feed.album

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.extractDominantColorFromUrl
import com.litbig.spotify.core.design.extension.gradientBackground
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
    val overlapHeightPx = with(LocalDensity.current) {
        EXPANDED_TOP_BAR_HEIGHT.toPx() - COLLAPSED_TOP_BAR_HEIGHT.toPx()
    }
    val isCollapsed: Boolean by remember {
        derivedStateOf {
            val isFirstItemHidden =
                listState.firstVisibleItemScrollOffset > overlapHeightPx
            isFirstItemHidden || listState.firstVisibleItemIndex > 0
        }
    }

    Box(
        modifier = modifier
    ) {
        CollapsedTopBar(
            modifier = Modifier.zIndex(2f),
            albumName = uiState.albumName,
            isCollapsed = isCollapsed
        )
        ExpandedTopBar(
            imageUrl = uiState.imageUrl,
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
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = uiState.albumName,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineLarge,
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
                            .background(color = MaterialTheme.colorScheme.background),
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
}

val COLLAPSED_TOP_BAR_HEIGHT = 90.dp
val EXPANDED_TOP_BAR_HEIGHT = 330.dp

@Composable
private fun ExpandedTopBar(
    modifier: Modifier = Modifier,
    imageUrl: String?,
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .height(EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT),
        contentAlignment = Alignment.BottomStart
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
            ,
            model = imageUrl,
            contentDescription = "Album Art",
            contentScale = ContentScale.Crop,
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
    isCollapsed: Boolean
) {
    val color: Color by animateColorAsState(
        if (isCollapsed) {
            MaterialTheme.colorScheme.background
        } else {
            Color.Transparent
        },
        label = ""
    )
    Box(
        modifier = modifier
            .background(color)
            .fillMaxWidth()
            .height(COLLAPSED_TOP_BAR_HEIGHT)
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        AnimatedVisibility(visible = isCollapsed) {
            Text(
                modifier = Modifier.padding(start = 60.dp, bottom = 4.dp),
                text = albumName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@DevicePreviews
@Composable
fun PreviewCollapsedTopBar() {
    SpotifyTheme {
        Column {
            CollapsedTopBar(
                albumName = "Album Name",
                isCollapsed = true
            )
            CollapsedTopBar(
                albumName = "Album Name",
                isCollapsed = false
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