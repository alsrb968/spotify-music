@file:OptIn(ExperimentalMaterial3Api::class)

package com.litbig.spotify.ui.home.track

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.core.design.extension.extractDominantColorFromUrl
import com.litbig.spotify.ui.components.*
import com.litbig.spotify.ui.models.TrackUiModel
import com.litbig.spotify.ui.shared.Loading
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewPlaylistUiModel
import com.litbig.spotify.ui.tooling.PreviewTrackUiModels
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TracksScreen(
    modifier: Modifier = Modifier,
    viewModel: TracksViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToArtist: (String) -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is TracksUiEffect.NavigateBack -> navigateBack()
                is TracksUiEffect.NavigateToArtist -> navigateToArtist(effect.artistId)
                is TracksUiEffect.ShowToast -> onShowSnackBar(effect.message)
            }
        }
    }

    when (val s = state) {
        is TracksUiState.Loading -> {
            Loading(modifier = modifier.fillMaxSize())
        }
        is TracksUiState.Ready -> {

            val context = LocalContext.current
            LaunchedEffect(Unit) {
                val dominantColor = extractDominantColorFromUrl(context, s.imageUrl)
                viewModel.sendIntent(TracksUiIntent.SetDominantColor(dominantColor))
            }

            TracksScreen(
                modifier = modifier,
                tracks = s.tracks,
                title = s.title,
                dominantColor = s.dominantColor,
                trackSelected = { viewModel.sendIntent(TracksUiIntent.SelectTrack(it)) },
                navigateBack = { viewModel.sendIntent(TracksUiIntent.NavigateBack) },
                navigateToArtist = { viewModel.sendIntent(TracksUiIntent.NavigateToArtist(it)) },
            )
        }
    }
}

@Composable
fun TracksScreen(
    modifier: Modifier = Modifier,
    tracks: List<TrackUiModel>,
    title: String,
    dominantColor: Color,
    trackSelected: (String) -> Unit,
    navigateBack: () -> Unit,
    navigateToArtist: (String) -> Unit,
) {
    ScrollableTopBarSurface(
        modifier = modifier,
        expandStyle = TopBarExpandStyle.TRACK,
        onBack = navigateBack,
        header = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomStart),
                    text = "포함",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        imageUrl = null,
        dominantColor = dominantColor,
        title = title,
        contentSpaceBy = 8.dp,
    ) {
        tracks.forEach { track ->
            TrackItem(
                track = track,
                onClick = { trackSelected(track.id) },
                onMore = { /* todo: onMore */ },
            )
        }
    }
}

@Composable
fun TrackItem(
    modifier: Modifier = Modifier,
    track: TrackUiModel,
    onClick: () -> Unit,
    onMore: () -> Unit,
) {
    var isShowBottomSheet by remember { mutableStateOf(false) }
    if (isShowBottomSheet) {
        MenuBottomSheet(
            modifier = Modifier,
            onShow = { isShowBottomSheet = it },
            header = {
                ListItemVerticalMedium(
                    imageUrl = track.imageUrl,
                    imageSize = 50.dp,
                    shape = RectangleShape,
                    title = track.name,
                    subtitle = track.artists,
                    onClick = {},
                )
            },
            content = {
                MenuIconItem(
                    imageVector = Icons.Default.Favorite,
                    title = "좋아요 표시한 곡에 추가",
                )
                MenuIconItem(
                    imageVector = Icons.Default.AddCircleOutline,
                    title = "플레이리스트에 추가",
                )
                MenuIconItem(
                    imageVector = Icons.Default.PermIdentity,
                    title = "아티스트로 이동하기",
                )
                MenuIconItem(
                    imageVector = Icons.Default.Share,
                    title = "공유",
                )
            }
        )
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ListItemVerticalMedium(
                modifier = Modifier
                    .weight(1f),
                imageUrl = track.imageUrl,
                imageSize = 70.dp,
                shape = RectangleShape,
                title = track.name,
                subtitle = track.artists,
                onClick = onClick,
            )

            ScalableIconButton(
                onClick = {
                    isShowBottomSheet = true
                },
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun TracksScreenPreview() {
    SpotifyTheme {
        TracksScreen(
            tracks = PreviewTrackUiModels,
            title = PreviewPlaylistUiModel.name,
            dominantColor = Color.Gray,
            trackSelected = {},
            navigateBack = {},
            navigateToArtist = {},
        )
    }
}