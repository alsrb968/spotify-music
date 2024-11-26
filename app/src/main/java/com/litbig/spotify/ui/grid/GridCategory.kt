package com.litbig.spotify.ui.grid

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicInfoPagingData
import com.litbig.spotify.util.ColorExtractor.getRandomPastelColor
import timber.log.Timber

@Composable
fun GridCategory(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    navigateToList: (MusicInfo) -> Unit,
    title: String,
    musicInfoPagingItems: LazyPagingItems<MusicInfo>
) {

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
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

        val isLoading = musicInfoPagingItems.loadState.refresh is LoadState.Loading

        LazyRow(state = rememberLazyListState()) {
            if (isLoading) {
                items(4) {
                    SkeletonGridCell(
                        modifier = Modifier.padding(15.dp),
                        shape = shape
                    )
                }
            } else {
                items(musicInfoPagingItems.itemCount) { index ->
                    Timber.d("index=$index")
                    val dominantColor = getRandomPastelColor()
                    musicInfoPagingItems[index]?.let { musicInfo ->
                        GridCell(
                            modifier = Modifier.padding(15.dp),
                            shape = shape,
                            imageUrl = musicInfo.imageUrl,
                            coreColor = dominantColor,
                            title = musicInfo.title,
                            artist = musicInfo.content,
                            album = musicInfo.title,
                            isPlayable = false,
                            onClick = { navigateToList(musicInfo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GridMiniCategory(
    modifier: Modifier = Modifier,
    title: String,
    musicInfoPagingItems: LazyPagingItems<MusicInfo>
) {

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
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

        val isLoading = musicInfoPagingItems.loadState.refresh is LoadState.Loading

        LazyHorizontalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            rows = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                vertical = 8.dp,
                horizontal = 16.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isLoading) {
                items(6) {
                    SkeletonGridMiniCell()
                }
            } else {
                items(musicInfoPagingItems.itemCount) { index ->
                    Timber.d("index=$index")
                    musicInfoPagingItems[index]?.let { musicInfo ->
                        GridMiniCell(
                            modifier = Modifier,
                            imageUrl = musicInfo.imageUrl,
                            title = musicInfo.title,
                            content = musicInfo.content,
                            onClick = { }
                        )
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
fun PreviewGridCategory() {
    SpotifyTheme {
        GridCategory(
            musicInfoPagingItems = PreviewMusicInfoPagingData.collectAsLazyPagingItems(),
            shape = RectangleShape,
            title = "Your top albums",
            navigateToList = {}
        )
    }
}

@DevicePreviews
@Composable
fun PreviewGridMiniCategory() {
    SpotifyTheme {
        GridMiniCategory(
            musicInfoPagingItems = PreviewMusicInfoPagingData.collectAsLazyPagingItems(),
            title = "Your top albums",
        )
    }
}