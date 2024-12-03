package com.litbig.spotify.ui.player

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.litbig.spotify.util.ColorExtractor.extractDominantColorFromUrl
import com.litbig.spotify.util.ConvertExtensions.toHumanReadableDuration

@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
    onCollapse: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        is PlayerUiState.Idle -> {

        }

        is PlayerUiState.Ready -> {
            PlayerScreen(
                modifier = modifier,
                uiState = state as PlayerUiState.Ready,
                actions = PlayerScreenActions(
                    onFavorite = viewModel::onFavorite,
                    onPlayOrPause = viewModel::onPlayOrPause,
                    onPrevious = viewModel::onPrevious,
                    onNext = viewModel::onNext,
                    onShuffle = viewModel::onShuffle,
                    onRepeat = viewModel::onRepeat,
                    onProgress = viewModel::onProgress
                ),
                onCollapse = onCollapse
            )
        }
    }
}

@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState.Ready,
    actions: PlayerScreenActions,
    onCollapse: () -> Unit,
) {
    val context = LocalContext.current
    var dominantColor by remember { mutableStateOf(Color.Transparent) }

    LaunchedEffect(uiState.nowPlaying.albumArtUrl) {
        dominantColor = extractDominantColorFromUrl(context, uiState.nowPlaying.albumArtUrl)
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(color = dominantColor)
    ) {

        Column(
            modifier = Modifier
                .width(400.dp)
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ControlPanelTop(
                modifier = Modifier,
                onCollapse = onCollapse
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize(),
                    model = uiState.nowPlaying.albumArtUrl,
                    contentDescription = "Album Art",
                    contentScale = ContentScale.Crop,
                    placeholder = shimmerPainter(),
                    error = painterResource(id = R.drawable.baseline_image_not_supported_24),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 8.dp),
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.TopStart),
                    text = uiState.nowPlaying.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.BottomStart),
                    text = uiState.nowPlaying.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ControlPanelProgress(
                modifier = Modifier
                    .fillMaxWidth(),
                uiState = uiState,
                actions = actions
            )

            ControlPanelBottom(
                modifier = Modifier
                    .fillMaxWidth(),
                uiState = uiState,
                actions = actions
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            state = rememberLazyListState(),
        ) {
            items(uiState.playList.size) { index ->
                val metadata = uiState.playList[index]
                PlayerCell(
                    isPlaying = index == uiState.indexOfList,
                    imageUrl = metadata.albumArtUrl,
                    title = metadata.title,
                    artist = metadata.artist,
                    isFavorite = uiState.isFavorite,
                    totalTime = metadata.duration.toLong().toHumanReadableDuration(),
                    onClick = {},
                    onFavorite = {},
                )
            }
        }
    }
}

@Composable
fun ControlPanelTop(
    modifier: Modifier = Modifier,
    onCollapse: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterStart),
            onClick = onCollapse
        ) {
            Icon(
                modifier = Modifier.size(36.dp),
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Down",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Now Playing",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Artist Name",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlPanelProgress(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState.Ready,
    actions: PlayerScreenActions,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Slider(
            value = uiState.playingTime.toFloat() / uiState.nowPlaying.duration.toLong(),
            onValueChange = { value ->
                actions.onProgress((value * uiState.nowPlaying.duration.toLong()).toLong())
            },
            onValueChangeFinished = { /*TODO*/ },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface)
                )
            },
            track = { sliderState ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Box(
                    Modifier
                        .fillMaxWidth(sliderState.value)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface)
                )
            }
        )

        Text(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp),
            text = uiState.playingTime.toHumanReadableDuration(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp),
            text = uiState.nowPlaying.duration.toLong().toHumanReadableDuration(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )

    }
}

@Composable
fun ControlPanelBottom(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState.Ready,
    actions: PlayerScreenActions,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = actions.onShuffle
        ) {
            Icon(
                painter = painterResource(
                    id =
                    if (uiState.isShuffle) R.drawable.property_1_shuffle_on
                    else R.drawable.property_1_shuffle_off
                ),
                contentDescription = "Shuffle",
                tint = Color.Unspecified
            )
        }

        IconButton(
            onClick = actions.onPrevious
        ) {
            Icon(
                painter = painterResource(id = R.drawable.property_1_previous),
                contentDescription = "Previous",
                tint = Color.Unspecified
            )
        }

        IconButton(
            modifier = Modifier.size(56.dp),
            onClick = actions.onPlayOrPause
        ) {
            Icon(
                modifier = Modifier.size(56.dp),
                painter = painterResource(
                    id =
                    if (uiState.isPlaying) R.drawable.property_1_pause
                    else R.drawable.property_1_play
                ),
                contentDescription = "Play",
                tint = Color.Unspecified
            )
        }

        IconButton(
            onClick = actions.onNext
        ) {
            Icon(
                painter = painterResource(id = R.drawable.property_1_next),
                contentDescription = "Next",
                tint = Color.Unspecified
            )
        }

        IconButton(
            onClick = actions.onRepeat
        ) {
            Icon(
                painter = painterResource(
                    id =
                    when (uiState.repeatMode) {
                        0 -> R.drawable.property_1_replay_off
                        1 -> R.drawable.property_1_replay_one
                        else -> R.drawable.property_1_replay_all
                    }
                ),
                contentDescription = "Repeat",
                tint = Color.Unspecified
            )
        }
    }
}

@DevicePreviews
@Composable
fun PreviewPlayerScreen() {
    SpotifyTheme {
        PlayerScreen(
            modifier = Modifier
                .size(width = 1024.dp, height = 500.dp),
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
            actions = PlayerScreenActions(
                onFavorite = {},
                onPlayOrPause = {},
                onPrevious = {},
                onNext = {},
                onShuffle = {},
                onRepeat = {},
                onProgress = {}
            ),
            onCollapse = {}
        )
    }
}

data class PlayerScreenActions(
    val onFavorite: () -> Unit,
    val onPlayOrPause: () -> Unit,
    val onPrevious: () -> Unit,
    val onNext: () -> Unit,
    val onShuffle: () -> Unit,
    val onRepeat: () -> Unit,
    val onProgress: (Long) -> Unit,
)
