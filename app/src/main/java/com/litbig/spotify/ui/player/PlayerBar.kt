package com.litbig.spotify.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.data.mapper.local.toLong
import com.litbig.spotify.core.design.component.shimmerPainter
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicMetadata
import com.litbig.spotify.ui.tooling.PreviewMusicMetadataList
import com.litbig.spotify.core.design.extension.extractDominantColorFromUrl

@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
    onExpand: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is PlayerUiState.Idle -> {
            // Do nothing
        }

        is PlayerUiState.Ready -> {

            val context = LocalContext.current
            LaunchedEffect(s.nowPlaying.albumArtUrl) {
                val color = extractDominantColorFromUrl(context, s.nowPlaying.albumArtUrl)
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
                onExpand = onExpand,
            )
        }
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
            .padding(
                horizontal = 8.dp,
            )
            .clip(RoundedCornerShape(6.dp))
            .background(color = uiState.dominantColor)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpand() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color = uiState.dominantColor),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(5.dp))
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = uiState.nowPlaying.albumArtUrl,
                        contentDescription = "Album Art",
                        contentScale = ContentScale.Crop,
                        placeholder = shimmerPainter(),
                        error = painterResource(id = R.drawable.baseline_image_not_supported_24),
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .padding(end = 150.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = uiState.nowPlaying.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = uiState.nowPlaying.artist,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
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
                progress = { uiState.playingTime.toFloat() / uiState.nowPlaying.duration.toLong() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .padding(horizontal = 10.dp)
                    .align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.onSurface,
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
            modifier = Modifier,
            onClick = actions.onFavorite,
        ) {
            Icon(
                imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(
            modifier = Modifier
                .size(48.dp),
            onClick = actions.onPlayOrPause,
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp),
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
                nowPlaying = PreviewMusicMetadata,
                playList = PreviewMusicMetadataList,
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