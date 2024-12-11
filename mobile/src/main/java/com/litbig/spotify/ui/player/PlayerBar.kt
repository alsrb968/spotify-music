package com.litbig.spotify.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litbig.spotify.core.design.extension.extractDominantColorFromUrl
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewTrackDetailsInfo
import com.litbig.spotify.ui.tooling.PreviewTrackDetailsInfos

@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
//    onExpand: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isShowPlayer by viewModel.isShowPlayer.collectAsStateWithLifecycle()

    when (val s = state) {
        is PlayerUiState.Idle -> {
            // Do nothing
        }

        is PlayerUiState.Ready -> {

            val context = LocalContext.current
            LaunchedEffect(s.nowPlaying.imageUrl) {
                val color = extractDominantColorFromUrl(context, s.nowPlaying.imageUrl)
                viewModel.setDominantColor(color)
            }

            PlayerBar(
                modifier = modifier,
                uiState = s,
                actions = PlayerBarActions(
                    onFavorite = viewModel::onFavorite,
                    onPlayOrPause = viewModel::onPlayOrPause,
                    onProgress = viewModel::onProgress,
                ),
                onExpand = {
                    viewModel.showPlayer(true)
                },
            )
        }
    }

    if (isShowPlayer) {
        PlayerBottomSheet()
    }
}

@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState.Ready,
    actions: PlayerBarActions,
    onExpand: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clickable { onExpand() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color = uiState.dominantColor),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(5.dp))
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = uiState.nowPlaying.imageUrl,
                        contentDescription = "Album Art",
                        contentScale = ContentScale.Crop,
                        placeholder = rememberVectorPainter(image = Icons.Default.Album),
                        error = rememberVectorPainter(image = Icons.Default.Error),
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .padding(end = 150.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = uiState.nowPlaying.title,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = uiState.nowPlaying.artist,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            ControlBar(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.CenterEnd),
                uiState = uiState,
                actions = actions,
            )

            LinearProgressIndicator(
                progress = { uiState.playingTime.toFloat() / uiState.nowPlaying.duration },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .padding(horizontal = 10.dp)
                    .align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.onSurface,
                gapSize = 0.dp,
                drawStopIndicator = {},
            )
        }
    }
}

@Composable
fun ControlBar(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState.Ready,
    actions: PlayerBarActions,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        IconButton(
            modifier = Modifier
                .size(36.dp),
            onClick = actions.onFavorite,
        ) {
            Icon(
                imageVector = if (uiState.isFavorite) Icons.Filled.CheckCircle else Icons.Outlined.AddCircleOutline,
                contentDescription = "Favorite",
                tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(
            modifier = Modifier
                .size(36.dp),
            onClick = actions.onPlayOrPause,
        ) {
            Icon(
                modifier = Modifier
                    .size(32.dp),
                imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play/Pause",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

data class PlayerBarActions(
    val onFavorite: () -> Unit,
    val onPlayOrPause: () -> Unit,
    val onProgress: (Long) -> Unit,
)

@DevicePreviews
@Composable
fun PreviewPlayerBar() {
    SpotifyTheme {
        PlayerBar(
            uiState = PlayerUiState.Ready(
                indexOfList = 0,
                nowPlaying = PreviewTrackDetailsInfo,
                playList = PreviewTrackDetailsInfos,
                playingTime = 159000,
                isPlaying = true,
                isShuffle = false,
                repeatMode = 0,
                isFavorite = false,
                dominantColor = Color.Gray
            ),
            actions = PlayerBarActions(
                onFavorite = {},
                onPlayOrPause = {},
                onProgress = {},
            ),
            onExpand = {},
        )
    }
}