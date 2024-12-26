package com.litbig.spotify.ui.home.track

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.core.design.extension.extractDominantColorFromUrl
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.ui.components.ListItemVerticalMedium
import com.litbig.spotify.ui.components.ScalableIconButton
import com.litbig.spotify.ui.components.ScrollableTopBarSurface
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

    LaunchedEffect(viewModel.effect) {
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
        onBack = navigateBack,
        header = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .gradientBackground(
                        ratio = 0.5f,
                        startColor = dominantColor,
                        endColor = MaterialTheme.colorScheme.background,
                    )
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
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ListItemVerticalMedium(
            modifier = Modifier
                .weight(1f),
            imageUrl = track.imageUrl,
            imageSize = 70.dp,
            title = track.name,
            subtitle = track.artists,
            onClick = onClick,
        )

        ScalableIconButton(
            onClick = onMore,
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = MaterialTheme.colorScheme.onSurface,
            )
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