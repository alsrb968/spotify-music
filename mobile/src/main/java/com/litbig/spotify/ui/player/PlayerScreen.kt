@file:OptIn(ExperimentalMaterial3Api::class)

package com.litbig.spotify.ui.player

import androidx.compose.animation.Crossfade
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.extractDominantColorFromUrl
import com.litbig.spotify.core.domain.extension.toHumanReadableDuration
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewTrackDetailsInfo
import com.litbig.spotify.ui.tooling.PreviewTrackDetailsInfos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun PlayerBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scope = rememberCoroutineScope()
    var playerBarHeight by remember { mutableStateOf(0.dp) }

    playerBarHeight = when (state) {
        is PlayerUiState.Idle -> {
            0.dp
        }

        is PlayerUiState.Ready -> {
            70.dp
        }
    }

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = sheetState
        ),
        sheetMaxWidth = LocalConfiguration.current.screenWidthDp.dp,
        sheetPeekHeight = playerBarHeight,
        sheetDragHandle = null,
        sheetContainerColor = Color.Transparent,
//        sheetShape = RoundedCornerShape(
//            topStart = 8.dp,
//            topEnd = 8.dp,
//            bottomStart = 0.dp,
//            bottomEnd = 0.dp
//        ),
        sheetContent = {

            Crossfade(targetState = sheetState.currentValue, label = "") {
                when (it) {
                    SheetValue.PartiallyExpanded -> {
                        PlayerBar {
                            scope.launch {
                                sheetState.expand()
                            }
                        }
                    }

                    SheetValue.Expanded -> {
                        PlayerScreen {
                            scope.launch {
                                sheetState.partialExpand()
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    ) {
        content()
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
                uiState = s,
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
    uiState: PlayerUiState.Ready,
    actions: PlayerScreenActions,
    onCollapse: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = uiState.dominantColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        ControlPanelTop(
            modifier = Modifier,
            onCollapse = onCollapse
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .size(230.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(5.dp),
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = uiState.nowPlaying.imageUrl,
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                placeholder = rememberVectorPainter(image = Icons.Default.Album),
                error = rememberVectorPainter(image = Icons.Default.Error),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 8.dp),
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(end = 50.dp)
                    .basicMarquee(),
                text = uiState.nowPlaying.title,
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
                text = uiState.nowPlaying.artist,
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
                    imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
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
            value = uiState.playingTime.toFloat() / uiState.nowPlaying.duration,
            onValueChange = { value ->
                actions.onProgress((value * uiState.nowPlaying.duration).toLong())
            },
            onValueChangeFinished = {

            },
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
            text = uiState.nowPlaying.duration.toHumanReadableDuration(),
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
                nowPlaying = PreviewTrackDetailsInfo,
                playList = PreviewTrackDetailsInfos,
                playingTime = 159000,
                isPlaying = true,
                isShuffle = false,
                repeatMode = 0,
                isFavorite = false,
                dominantColor = Color.DarkGray
            ),
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