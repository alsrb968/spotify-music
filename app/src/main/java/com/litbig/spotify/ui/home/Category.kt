package com.litbig.spotify.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.ui.grid.GridCell
import com.litbig.spotify.ui.grid.GridMiniCell
import com.litbig.spotify.ui.grid.SkeletonGridCell
import com.litbig.spotify.ui.grid.SkeletonGridMiniCell
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicInfoPagingData
import com.litbig.spotify.util.ColorExtractor.getRandomPastelColor

@Composable
fun Category(
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
        CategoryTitle(
            title = title,
            onSeeAllClick = { /*TODO*/ }
        )

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
                val limitedItems = musicInfoPagingItems.itemSnapshotList.take(8)
                items(limitedItems.size) { index ->
                    val dominantColor = remember { getRandomPastelColor() }
                    limitedItems[index]?.let { musicInfo ->
                        GridCell(
                            modifier = Modifier.padding(15.dp),
                            shape = shape,
                            imageUrl = musicInfo.imageUrl,
                            coreColor = dominantColor,
                            title = musicInfo.title,
                            artist = musicInfo.content,
                            isPlayable = false,
                            onClick = { navigateToList(musicInfo) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))
    }
}

@Composable
fun MiniCategory(
    modifier: Modifier = Modifier,
    title: String,
    musicInfoPagingItems: LazyPagingItems<MusicInfo>
) {

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        CategoryTitle(
            title = title,
            onSeeAllClick = { /*TODO*/ }
        )

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
                val limitedItems = musicInfoPagingItems.itemSnapshotList.take(8)
                items(limitedItems.size) { index ->
                    limitedItems[index]?.let { musicInfo ->
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

        Spacer(modifier = Modifier.height(26.dp))
    }
}

@Composable
fun CategoryTitle(
    modifier: Modifier = Modifier,
    title: String,
    onSeeAllClick: () -> Unit
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

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clickable { onSeeAllClick() }
        ) {
            Text(
                modifier = Modifier
                    .padding(4.dp),
                text = "SEE ALL",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@DevicePreviews
@Composable
fun PreviewCategory() {
    SpotifyTheme {
        Category(
            musicInfoPagingItems = PreviewMusicInfoPagingData.collectAsLazyPagingItems(),
            shape = RectangleShape,
            title = "Your top albums",
            navigateToList = {}
        )
    }
}

@DevicePreviews
@Composable
fun PreviewMiniCategory() {
    SpotifyTheme {
        MiniCategory(
            musicInfoPagingItems = PreviewMusicInfoPagingData.collectAsLazyPagingItems(),
            title = "Your top albums",
        )
    }
}