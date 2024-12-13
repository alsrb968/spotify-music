@file:OptIn(ExperimentalMaterial3Api::class)

package com.litbig.spotify.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.extractDominantColorFromUrl
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.core.domain.extension.toHumanReadableDuration
import com.litbig.spotify.ui.models.TrackUiModel
import com.litbig.spotify.ui.player.cards.ArtistDetailsInfoCard
import com.litbig.spotify.ui.player.cards.TrackDetailsInfoCard
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewTrackUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun PlayerBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = {
            viewModel.showPlayer(false)
        },
        sheetState = sheetState,
        sheetMaxWidth = LocalConfiguration.current.screenWidthDp.dp,
        dragHandle = null,
        containerColor = Color.Transparent,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = true,
        ),
    ) {
        PlayerScreen(
            onCollapse = {
                viewModel.showPlayer(false)
            }
        )
    }
}

@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
    onCollapse: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is PlayerUiState.Idle -> {

        }

        is PlayerUiState.Ready -> {

            val context = LocalContext.current
            LaunchedEffect(s.nowPlaying.imageUrl) {
                val color = extractDominantColorFromUrl(context, s.nowPlaying.imageUrl)
                viewModel.setDominantColor(color)
            }

            PlayerScreen(
                modifier = modifier,
                nowPlaying = s.nowPlaying,
                playingTime = s.playingTime,
                isPlaying = s.isPlaying,
                isShuffle = s.isShuffle,
                repeatMode = s.repeatMode,
                isFavorite = s.isFavorite,
                dominantColor = s.dominantColor,
                actions = PlayerScreenActions(
                    isFavorite = viewModel::isFavoriteTrack,
                    onFavorite = viewModel::onFavorite,
                    onFavoriteIndex = viewModel::onFavoriteIndex,
                    onPlayOrPause = viewModel::onPlayOrPause,
                    onPlayIndex = viewModel::onPlayIndex,
                    onPrevious = viewModel::onPrevious,
                    onNext = viewModel::onNext,
                    onShuffle = viewModel::onShuffle,
                    onRepeat = viewModel::onRepeat,
                    onProgress = viewModel::onProgress
                ),
                onCollapse = onCollapse,
            )
        }
    }
}

@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    nowPlaying: TrackUiModel,
    playingTime: Long,
    isPlaying: Boolean,
    isShuffle: Boolean,
    repeatMode: Int,
    isFavorite: Boolean,
    dominantColor: Color,
    actions: PlayerScreenActions,
    onCollapse: () -> Unit,
) {
    val listState = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .gradientBackground(
                ratio = 1f,
                startColor = dominantColor,
                endColor = MaterialTheme.colorScheme.background
            ),
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState,
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    ControlPanelTop(
                        modifier = Modifier,
                        onCollapse = onCollapse
                    )

                    Spacer(modifier = Modifier.height(100.dp))

                    SquareCard {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxSize(),
                            model = nowPlaying.imageUrl,
                            contentDescription = "Album Art",
                            contentScale = ContentScale.Crop,
                            placeholder = rememberVectorPainter(image = Icons.Default.Album),
                            error = rememberVectorPainter(image = Icons.Default.Error),
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(start = 4.dp),
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .fillMaxWidth()
                                .padding(end = 50.dp)
                                .basicMarquee(),
                            text = nowPlaying.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .padding(end = 50.dp)
                                .basicMarquee(),
                            text = nowPlaying.artists,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )

                        IconButton(
                            modifier = Modifier
                                .align(Alignment.CenterEnd),
                            onClick = actions.onFavorite,
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    ControlPanelProgress(
                        modifier = Modifier
                            .fillMaxWidth(),
                        nowPlaying = nowPlaying,
                        playingTime = playingTime,
                        actions = actions
                    )

                    ControlPanelBottom(
                        modifier = Modifier
                            .fillMaxWidth(),
                        isPlaying = isPlaying,
                        isShuffle = isShuffle,
                        repeatMode = repeatMode,
                        actions = actions
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }

            }

            item {
                Spacer(modifier = Modifier.height(105.dp))
                ArtistDetailsInfoCard()
            }

            item {
                TrackDetailsInfoCard()
            }
        }
    }
}

@Composable
fun SquareCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        content = {
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                content()
            }
        },
        modifier = modifier
    ) { measurables, constraints ->
        // 가로와 세로 중 최소값을 기준으로 1:1 크기 계산
        val size = constraints.maxWidth.coerceAtMost(constraints.maxHeight)
        val imageConstraints = constraints.copy(
            minWidth = size,
            maxWidth = size,
            minHeight = size,
            maxHeight = size
        )

        // 이미지 측정
        val placeable = measurables.first().measure(imageConstraints)

        layout(size, size) {
            // 이미지 배치
            placeable.placeRelative(0, 0)
        }
    }
}

data class PlayerScreenActions(
    val isFavorite: (String) -> Flow<Boolean>,
    val onFavorite: () -> Unit,
    val onFavoriteIndex: (Int) -> Unit,
    val onPlayOrPause: () -> Unit,
    val onPlayIndex: (Int) -> Unit,
    val onPrevious: () -> Unit,
    val onNext: () -> Unit,
    val onShuffle: () -> Unit,
    val onRepeat: () -> Unit,
    val onProgress: (Long) -> Unit,
)

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
                .size(36.dp)
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
                style = MaterialTheme.typography.bodyMedium,
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

@Composable
fun ControlPanelProgress(
    modifier: Modifier = Modifier,
    nowPlaying: TrackUiModel,
    playingTime: Long,
    actions: PlayerScreenActions,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Slider(
            value = playingTime.toFloat() / nowPlaying.duration,
            onValueChange = { value ->
                actions.onProgress((value * nowPlaying.duration).toLong())
            },
            onValueChangeFinished = {

            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center)
                        .background(MaterialTheme.colorScheme.onSurface)
                )
            },
            track = { sliderState ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Box(
                    Modifier
                        .fillMaxWidth(sliderState.value)
                        .height(2.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface)
                )
            }
        )

        Text(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 4.dp),
            text = playingTime.toHumanReadableDuration(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 4.dp),
            text = nowPlaying.duration.toHumanReadableDuration(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

    }
}

@Composable
fun ControlPanelBottom(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isShuffle: Boolean,
    repeatMode: Int,
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
                    if (isShuffle) R.drawable.property_1_shuffle_on
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
                    if (isPlaying) R.drawable.property_1_pause
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
                    when (repeatMode) {
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
fun PlayerScreenPreview() {
    SpotifyTheme {
        PlayerScreen(
            nowPlaying = PreviewTrackUiModel,
            playingTime = 159000,
            isPlaying = true,
            isShuffle = false,
            repeatMode = 0,
            isFavorite = false,
            dominantColor = Color.DarkGray,
            actions = PlayerScreenActions(
                isFavorite = { flowOf(false) },
                onFavorite = {},
                onFavoriteIndex = {},
                onPlayOrPause = {},
                onPlayIndex = {},
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