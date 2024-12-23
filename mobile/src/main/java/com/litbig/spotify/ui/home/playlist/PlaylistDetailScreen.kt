package com.litbig.spotify.ui.home.playlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.extractDominantColorFromUrl
import com.litbig.spotify.ui.components.ListItemHorizontalMedium
import com.litbig.spotify.ui.components.ListItemVerticalMedium
import com.litbig.spotify.ui.components.ListTitle
import com.litbig.spotify.ui.components.ScrollableTopBarSurface
import com.litbig.spotify.ui.home.artist.SimpleTopTrackListInfo
import com.litbig.spotify.ui.models.OwnerUiModel
import com.litbig.spotify.ui.models.PlaylistUiModel
import com.litbig.spotify.ui.models.TrackUiModel
import com.litbig.spotify.ui.shared.Loading
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.*
import java.text.NumberFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PlaylistDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: PlaylistDetailViewModel = hiltViewModel(),
    navigateToPlaylist: (String) -> Unit,
    navigateBack: () -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        PlaylistDetailUiState.Loading -> {
            Loading(modifier = modifier.fillMaxSize())
        }
        is PlaylistDetailUiState.Ready -> {

            val context = LocalContext.current
            LaunchedEffect(Unit) {
                val dominantColor = extractDominantColorFromUrl(context, s.playlist.imageUrl)
                viewModel.sendIntent(PlaylistDetailUiIntent.SetDominantColor(dominantColor))
            }

            PlaylistDetailScreen(
                modifier = modifier,
                playlist = s.playlist,
                tracks = s.tracks,
                otherPlaylists = s.otherPlaylists,
                owner = s.owner,
                navigateToPlaylist = navigateToPlaylist,
                navigateBack = navigateBack,
            )
        }
    }
}

@Composable
fun PlaylistDetailScreen(
    modifier: Modifier = Modifier,
    playlist: PlaylistUiModel,
    tracks: List<TrackUiModel>,
    otherPlaylists: List<PlaylistUiModel>,
    owner: OwnerUiModel,
    navigateToPlaylist: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    ScrollableTopBarSurface(
        modifier = modifier,
        imageUrl = playlist.imageUrl,
        dominantColor = playlist.dominantColor,
        title = playlist.name,
        onBack = navigateBack,
        header = {
            PlaylistInfoTitle(
                modifier = it,
                playlist = playlist,
                tracks = tracks,
                owner = owner,
            )
        }
    ) {
        SimpleTopTrackListInfo(
            tracks = tracks,
            onClick = { /* todo */ }
        )

        ListTitle(title = "다른 추천 항목") {
            val items = otherPlaylists.take(6)
            LazyVerticalGrid(
                modifier = Modifier
                    .height(800.dp),
                state = rememberLazyGridState(),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(items.size) { index ->
                    val other = items[index]
                    ListItemHorizontalMedium(
                        modifier = Modifier
                            .height(250.dp),
                        size = 180.dp,
                        imageUrl = other.imageUrl,
                        title = other.name,
                        subtitle = other.description,
                        onClick = { navigateToPlaylist(other.id) }
                    )
                }
            }
        }
    }

}

@Composable
fun PlaylistInfoTitle(
    modifier: Modifier = Modifier,
    playlist: PlaylistUiModel,
    tracks: List<TrackUiModel>,
    owner: OwnerUiModel,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = playlist.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(32.dp),
                model = owner.imageUrl,
                contentDescription = "Owner profile image",
                contentScale = ContentScale.Crop,
                placeholder = rememberVectorPainter(image = Icons.Default.Album),
                error = rememberVectorPainter(image = Icons.Default.Error),
            )

            Text(
                text = owner.name,
                modifier = Modifier,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val savedCount = NumberFormat.getNumberInstance(Locale.US)
            .format(playlist.follower)
        val duration = tracks.sumOf { it.duration }.milliseconds
        val time = if (duration.inWholeHours > 0) {
            "%s시간 %s분".format(duration.inWholeHours, duration.inWholeMinutes % 60)
        } else {
            "%s분".format(duration.inWholeMinutes)
        }

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                modifier = Modifier
                    .size(22.dp),
                imageVector = Icons.Default.Language,
                contentDescription = "Saved count",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "저장 횟수: $savedCount ∙ $time",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { /* todo */ }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowCircleDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { /* todo */ }
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { /* todo */ }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.property_1_shuffle_on),
                    contentDescription = "Shuffle",
                    tint = Color.Unspecified
                )
            }

            FloatingActionButton(
                modifier = Modifier,
                shape = CircleShape,
                onClick = { /* todo */ },
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

@DevicePreviews
@Composable
private fun PlaylistDetailScreenPreview() {
    SpotifyTheme {
        PlaylistDetailScreen(
            playlist = PreviewPlaylistUiModel,
            tracks = PreviewTrackUiModels,
            otherPlaylists = PreviewPlaylistUiModels,
            owner = PreviewOwnerUiModel,
            navigateToPlaylist = {},
            navigateBack = {},
        )
    }
}