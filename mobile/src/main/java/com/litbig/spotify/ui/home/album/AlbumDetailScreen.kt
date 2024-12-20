@file:OptIn(
    ExperimentalFoundationApi::class
)
@file:Suppress("UNNECESSARY_SAFE_CALL", "USELESS_ELVIS")

package com.litbig.spotify.ui.home.album

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.core.design.extension.extractDominantColorFromUrl
import com.litbig.spotify.ui.components.*
import com.litbig.spotify.ui.home.artist.PlaylistInfo
import com.litbig.spotify.ui.models.AlbumUiModel
import com.litbig.spotify.ui.models.ArtistUiModel
import com.litbig.spotify.ui.models.PlaylistUiModel
import com.litbig.spotify.ui.models.TrackUiModel
import com.litbig.spotify.ui.shared.Loading
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.*
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AlbumDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AlbumDetailUiEffect.ShowToast -> onShowSnackBar(effect.message)
            }
        }
    }

    when (val s = state) {
        is AlbumDetailUiState.Loading -> {
            Loading(modifier = modifier.fillMaxWidth())
        }

        is AlbumDetailUiState.Ready -> {

            val context = LocalContext.current
            LaunchedEffect(Unit) {
                val dominantColor = extractDominantColorFromUrl(context, s.album.imageUrl)
                viewModel.sendIntent(AlbumDetailUiIntent.SetDominantColor(dominantColor))
            }

            AlbumDetailScreen(
                modifier = modifier,
                album = s.album,
                artists = s.artists,
                tracks = s.tracks,
                playlists = s.playlists,
                playingTrackId = s.playingTrackId,
                onPlayTrack = { trackId ->
                    viewModel.sendIntent(AlbumDetailUiIntent.PlayTrack(trackId))
                },
                onPlayTracks = { trackIds ->
                    viewModel.sendIntent(AlbumDetailUiIntent.PlayTracks(trackIds))
                },
                onAddTrack = { trackId ->
                    viewModel.sendIntent(AlbumDetailUiIntent.AddTrack(trackId))
                },
                onAddTracks = { trackIds ->
                    viewModel.sendIntent(AlbumDetailUiIntent.AddTracks(trackIds))
                },
                onToggleFavoriteAlbum = { albumId ->
                    viewModel.sendIntent(AlbumDetailUiIntent.ToggleFavoriteAlbum(albumId))
                },
                onToggleFavoriteTrack = { trackId ->
                    viewModel.sendIntent(AlbumDetailUiIntent.ToggleFavoriteTrack(trackId))
                },
                navigateBack = navigateBack
            )
        }
    }
}

@Composable
fun AlbumDetailScreen(
    modifier: Modifier = Modifier,
    album: AlbumUiModel,
    artists: List<ArtistUiModel>,
    tracks: List<TrackUiModel>,
    playlists: List<PlaylistUiModel>,
    playingTrackId: String?,
    onPlayTrack: (String) -> Unit,
    onPlayTracks: (List<String>) -> Unit,
    onAddTrack: (String) -> Unit,
    onAddTracks: (List<String>) -> Unit,
    onToggleFavoriteAlbum: (String) -> Unit,
    onToggleFavoriteTrack: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    ScrollableTopBarSurface(
        modifier = modifier,
        imageUrl = album.imageUrl,
        dominantColor = album.dominantColor,
        title = album.name,
        onBack = navigateBack,
        header = { mod ->
            AlbumInfoTitle(
                modifier = mod,
                album = album,
                artists = artists,
                tracksTotalTime = album.totalTime,
                onPlayTracks = {
                    tracks.map { track -> track.id }?.let {
                        onPlayTracks(it)
                    }
                }
            )
        }
    ) {
        SimpleTrackListInfo(
            tracks = tracks,
            onClick = { /* todo */ }
        )

        ListTitle(
            title = SimpleDateFormat("M월 d일, yyyy", Locale.KOREA)
            .format(album.releaseDate)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            artists?.forEach { artist ->
                ListItemVerticalMedium(
                    imageUrl = artist.imageUrl,
                    imageSize = 60.dp,
                    shape = CircleShape,
                    title = artist.name,
                    onClick = { /* todo */ }
                )
            }
        }

        ListTitle(title = "이건 어떠신가요") {
            PlaylistInfo(
                playlists = playlists,
                onPlaylist = { /* todo */ }
            )
        }

        album.copyright?.let {
            Text(
                text = album.copyright,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
fun AlbumInfoTitle(
    modifier: Modifier = Modifier,
    album: AlbumUiModel,
    artists: List<ArtistUiModel>?,
    tracksTotalTime: Long,
    onPlayTracks: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        ArtistsOfAlbum(
            artists = artists ?: emptyList(),
            onClick = { /* todo */ }
        )

        Spacer(modifier = Modifier.height(8.dp))

        val duration = tracksTotalTime.milliseconds
        val time = if (duration.inWholeHours > 0) {
            "%s시간 %s분".format(duration.inWholeHours, duration.inWholeMinutes % 60)
        } else {
            "%s분".format(duration.inWholeMinutes)
        }
        val type = when (album.albumType) {
            "album" -> "앨범"
            "single" -> "싱글"
            "compilation" -> "EP"
            else -> ""
        }
        val year = SimpleDateFormat("yyyy", Locale.US)
            .format(album.releaseDate)
        Text(
            text = "$type ∙ $year",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

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
                onClick = onPlayTracks,
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

@Composable
fun ArtistsOfAlbum(
    modifier: Modifier = Modifier,
    artists: List<ArtistUiModel>,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickableScaled { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box {
            val artistList = artists.take(3)
            val size = artistList.size
            artistList.forEachIndexed { index, artist ->
                Box(
                    modifier = Modifier
                        .padding(start = (size - 1 - index) * 14.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        model = artist.imageUrl,
                        contentDescription = "Artist Image",
                        placeholder = rememberVectorPainter(image = Icons.Default.Album),
                        error = rememberVectorPainter(image = Icons.Default.Error),
                    )
                }
            }
        }

        val artistNames = if (artists.size > 2)
            "${artists[0].name}, ${artists[1].name} 외 ${artists.size - 2}명"
        else
            artists.joinToString { it.name }

        Text(
            text = artistNames,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }

}

@Composable
fun IconButtonWithText(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickableScaled { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier,
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SimpleTrackListInfo(
    modifier: Modifier = Modifier,
    tracks: List<TrackUiModel>,
    onClick: () -> Unit,
) {
    val text = buildAnnotatedString {
        tracks.forEachIndexed { index, track ->
            if (index == 5) {
                append(
                    AnnotatedString(
                        text = "더 보기",
                    )
                )
                return@buildAnnotatedString
            }

            append(
                AnnotatedString(
                    text = track.name,
                )
            )

            if (index != tracks.lastIndex) {
                append(
                    AnnotatedString(
                        text = " ∙ ",
                    )
                )
            }
        }
    }

    Text(
        modifier = modifier
            .clickableScaled { onClick() },
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 32.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@DevicePreviews
@Composable
private fun AlbumDetailScreenPreview() {
    SpotifyTheme {
        AlbumDetailScreen(
            album = PreviewAlbumUiModel,
            artists = PreviewArtistUiModels,
            tracks = PreviewTrackUiModels,
            playlists = PreviewPlaylistUiModels,
            playingTrackId = null,
            onPlayTrack = {},
            onPlayTracks = {},
            onAddTrack = {},
            onAddTracks = {},
            onToggleFavoriteAlbum = {},
            onToggleFavoriteTrack = {},
            navigateBack = {}
        )
    }
}