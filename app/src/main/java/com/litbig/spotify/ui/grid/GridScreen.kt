package com.litbig.spotify.ui.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.litbig.spotify.core.domain.model.Album
import com.litbig.spotify.core.domain.model.Artist
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewAlbumPagingData
import com.litbig.spotify.ui.tooling.PreviewArtistPagingData
import com.litbig.spotify.util.ColorExtractor.getRandomPastelColor
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

@Composable
fun GridScreen(
    viewModel: GridViewModel = hiltViewModel(),
    navigateToList: (MusicInfo) -> Unit
) {
    GridScreen(
        navigateToList = navigateToList,
        albumsPagingFlow = viewModel.albumsPagingFlow,
        artistPagingFlow = viewModel.artistPagingFlow
    )
}

@Composable
fun GridScreen(
    modifier: Modifier = Modifier,
    albumsPagingFlow: Flow<PagingData<Album>>,
    artistPagingFlow: Flow<PagingData<Artist>>,
    navigateToList: (MusicInfo) -> Unit
) {
    val albumsPagingItems = albumsPagingFlow.collectAsLazyPagingItems()
    val artistPagingItems = artistPagingFlow.collectAsLazyPagingItems()

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .gradientBackground(
                        ratio = 0.5f,
                        startColor = getRandomPastelColor(),
                        endColor = MaterialTheme.colorScheme.surfaceDim
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Your top albums",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        text = "SEE ALL",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                LazyRow(state = rememberLazyListState()) {
                    items(albumsPagingItems.itemCount) { index ->
                        Timber.d("index=$index")
                        val album = albumsPagingItems[index]
                        val dominantColor = getRandomPastelColor()

                        Spacer(modifier = Modifier.width(30.dp))

                        val musicInfo = MusicInfo(
                            imageUrl = album?.imageUrl,
                            title = album?.name ?: "",
                            content = album?.let { "${it.artist} • ${it.musicCount} songs" } ?: "",
                            category = "album"
                        )

                        GridCell(
                            imageUrl = musicInfo.imageUrl,
                            coreColor = dominantColor,
                            title = musicInfo.title,
                            artist = musicInfo.content,
                            album = musicInfo.title,
                            isPlayable = false,
                            onClick = { album?.let { navigateToList(musicInfo) } }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Your top artists",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        text = "SEE ALL",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                LazyRow(state = rememberLazyListState()) {
                    items(artistPagingItems.itemCount) { index ->
                        Timber.i("index=$index")
                        val artist = artistPagingItems[index]
                        val dominantColor = getRandomPastelColor()

                        Spacer(modifier = Modifier.width(30.dp))

                        val musicInfo = MusicInfo(
                            imageUrl = artist?.imageUrl,
                            title = artist?.name ?: "",
                            content = artist?.let { "${it.albumCount} albums • ${it.musicCount} songs" } ?: "",
                            category = "artist"
                        )

                        GridCell(
                            shape = CircleShape,
                            imageUrl = musicInfo.imageUrl,
                            coreColor = dominantColor,
                            title = musicInfo.title,
                            artist = musicInfo.content,
                            album = musicInfo.title,
                            isPlayable = false,
                            onClick = { artist?.let { navigateToList(musicInfo) } }
                        )
                    }
                }
            }
        }
    }
}

fun Modifier.gradientBackground(
    ratio: Float = 0.3f,
    startColor: Color,
    endColor: Color
): Modifier = composed {
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    this
        .onSizeChanged { boxSize = it }
        .background(
            brush = Brush.linearGradient(
                colors = listOf(startColor, endColor),
                start = Offset(0f, 0f),
                end = Offset(0f, boxSize.height * ratio)
            )
        )
}

@DevicePreviews
@Composable
fun PreviewGridScreen() {
    SpotifyTheme {
        GridScreen(
            navigateToList = {},
            albumsPagingFlow = PreviewAlbumPagingData,
            artistPagingFlow = PreviewArtistPagingData
        )
    }
}