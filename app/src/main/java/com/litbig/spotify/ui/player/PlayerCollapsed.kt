package com.litbig.spotify.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
import com.litbig.spotify.util.ConvertExtensions.toHumanReadableDuration

@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
    navigateToPlayer: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is PlayerUiState.Idle -> {
            // Do nothing
        }

        is PlayerUiState.Ready -> {
            PlayerBar(
                modifier = modifier,
                uiState = s,
                actions = PlayerBarActions(
                    onFavorite = viewModel::onFavorite,
                    onPlayOrPause = viewModel::onPlayOrPause,
                    onPrevious = viewModel::onPrevious,
                    onNext = viewModel::onNext,
                    onShuffle = viewModel::onShuffle,
                    onRepeat = viewModel::onRepeat,
                    onProgress = viewModel::onProgress,
                ),
                navigateToPlayer = navigateToPlayer,
            )
        }
    }
}

@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState.Ready,
    actions: PlayerBarActions,
    navigateToPlayer: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .background(color = MaterialTheme.colorScheme.surfaceContainer),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(18.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
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
                    .widthIn(max = 150.dp)
                    .height(43.dp),
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

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

            Spacer(modifier = Modifier.width(16.dp))

            ControlBar(
                uiState = uiState,
                actions = actions,
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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Image(
                modifier = Modifier
                    .size(32.dp)
                    .clickable { actions.onShuffle() },
                painter = painterResource(id = R.drawable.shuffle_s),
                contentDescription = "Shuffle Button",
            )

            Spacer(modifier = Modifier.width(22.dp))

            Image(
                modifier = Modifier
                    .size(32.dp)
                    .clickable { actions.onPrevious() },
                painter = painterResource(id = R.drawable.property_1_prev_s),
                contentDescription = "Previous Button",
            )

            Spacer(modifier = Modifier.width(22.dp))

            Image(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { actions.onPlayOrPause() },
                painter = painterResource(id = R.drawable.property_1_pause),
                contentDescription = "Play/Pause Button",
            )

            Spacer(modifier = Modifier.width(22.dp))

            Image(
                modifier = Modifier
                    .size(32.dp)
                    .clickable { actions.onNext() },
                painter = painterResource(id = R.drawable.property_1_next_s),
                contentDescription = "Next Button",
            )

            Spacer(modifier = Modifier.width(22.dp))

            Image(
                modifier = Modifier
                    .size(32.dp)
                    .clickable { actions.onRepeat() },
                painter = painterResource(id = R.drawable.repeat_s),
                contentDescription = "Repeat Button",
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val playingTime = uiState.playingTime
            val totalTime = uiState.nowPlaying.duration.toLong()
            val progress = playingTime.toFloat() / totalTime.toFloat()

            Text(
                text = playingTime.toHumanReadableDuration(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.width(8.dp))

            RoundedMusicProgressBar(
                modifier = Modifier
                    .width(250.dp),
                progress = progress,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = totalTime.toHumanReadableDuration(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

        }
    }
}

data class PlayerBarActions(
    val onFavorite: () -> Unit,
    val onPlayOrPause: () -> Unit,
    val onPrevious: () -> Unit,
    val onNext: () -> Unit,
    val onShuffle: () -> Unit,
    val onRepeat: () -> Unit,
    val onProgress: (Long) -> Unit,
)

@Composable
fun RoundedMusicProgressBar(
    modifier: Modifier = Modifier,
    progress: Float,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(color = MaterialTheme.colorScheme.onSurface)
        )
    }
}

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
            ),
            actions = PlayerBarActions(
                onFavorite = {},
                onPlayOrPause = {},
                onPrevious = {},
                onNext = {},
                onShuffle = {},
                onRepeat = {},
                onProgress = {},
            ),
            navigateToPlayer = {},
        )
    }
}

@DevicePreviews
@Composable
fun PreviewControlBar() {
    SpotifyTheme {
        ControlBar(
            uiState = PlayerUiState.Ready(
                indexOfList = 0,
                nowPlaying = PreviewMusicMetadata,
                playList = PreviewMusicMetadataList,
                playingTime = 159000,
                isPlaying = true,
                isShuffle = false,
                repeatMode = 0,
                isFavorite = false,
            ),
            actions = PlayerBarActions(
                onFavorite = {},
                onPlayOrPause = {},
                onPrevious = {},
                onNext = {},
                onShuffle = {},
                onRepeat = {},
                onProgress = {},
            )
        )
    }
}