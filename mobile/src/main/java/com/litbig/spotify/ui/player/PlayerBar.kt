package com.litbig.spotify.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import com.litbig.spotify.ui.models.TrackUiModel
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.*
import kotlinx.coroutines.flow.collectLatest

val PLAYER_BAR_HEIGHT = 60.dp

@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
    onShowSnackBar: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isShowPlayer by viewModel.isShowPlayer.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PlayerUiEffect.ShowToast -> onShowSnackBar(effect.message)
            }
        }
    }

    when (val s = state) {
        is PlayerUiState.Idle -> {

        }

        is PlayerUiState.Ready -> {

            val context = LocalContext.current
            LaunchedEffect(s.nowPlaying.imageUrl) {
                val color = extractDominantColorFromUrl(context, s.nowPlaying.imageUrl)
                viewModel.sendIntent(PlayerUiIntent.SetDominantColor(color))
            }

            PlayerBar(
                modifier = modifier,
                nowPlaying = s.nowPlaying,
                playingTime = s.playingTime,
                isPlaying = s.isPlaying,
                isFavorite = s.isFavorite,
                dominantColor = s.dominantColor,
                actions = PlayerBarActions(
                    onFavorite = {
                        viewModel.sendIntent(PlayerUiIntent.Favorite)
                    },
                    onPlayOrPause = {
                        viewModel.sendIntent(PlayerUiIntent.PlayOrPause)
                    },
                    onProgress = { position ->
                        viewModel.sendIntent(PlayerUiIntent.Progress(position))
                    },
                ),
                onExpand = {
                    viewModel.sendIntent(PlayerUiIntent.ShowPlayer(true))
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
    nowPlaying: TrackUiModel,
    playingTime: Long,
    isPlaying: Boolean,
    isFavorite: Boolean,
    dominantColor: Color,
    actions: PlayerBarActions,
    onExpand: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clickable { onExpand() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(PLAYER_BAR_HEIGHT)
                .clip(RoundedCornerShape(6.dp))
                .background(color = dominantColor),
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
                    model = nowPlaying.imageUrl,
                    contentDescription = "Album Art",
                    contentScale = ContentScale.Crop,
                    placeholder = rememberVectorPainter(image = Icons.Default.Album),
                    error = rememberVectorPainter(image = Icons.Default.Error),
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .basicMarquee(),
                    text = nowPlaying.name,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .basicMarquee(),
                    text = nowPlaying.artists,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            ControlBar(
                modifier = Modifier
                    .padding(end = 16.dp),
                isFavorite = isFavorite,
                isPlaying = isPlaying,
                actions = actions,
            )
        }

        LinearProgressIndicator(
            progress = { playingTime.toFloat() / nowPlaying.duration },
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

@Composable
fun ControlBar(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    isPlaying: Boolean,
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
                imageVector = if (isFavorite) Icons.Filled.CheckCircle else Icons.Outlined.AddCircleOutline,
                contentDescription = "Favorite",
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
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
private fun PlayerBarPreview() {
    SpotifyTheme {
        PlayerBar(
            nowPlaying = PreviewTrackUiModel,
            playingTime = 159000,
            isPlaying = true,
            isFavorite = false,
            dominantColor = Color.Transparent,
            actions = PlayerBarActions(
                onFavorite = {},
                onPlayOrPause = {},
                onProgress = {},
            ),
            onExpand = {},
        )
    }
}