package com.litbig.spotify.ui.home.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.ui.home.CategoryUiState
import com.litbig.spotify.ui.grid.GridCell
import com.litbig.spotify.ui.grid.GridMiniCell
import com.litbig.spotify.ui.grid.SkeletonGridCell
import com.litbig.spotify.ui.grid.SkeletonGridMiniCell
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicInfoList

@Composable
fun Category(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    navigateToList: (MusicInfo) -> Unit,
    title: String,
    onSeeAll: (String) -> Unit,
    categoryState: CategoryUiState,
) {
    val category = remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        CategoryTitle(
            title = title,
            onSeeAllClick = {
                onSeeAll(category.value)
            }
        )

        LazyRow(
            state = rememberLazyListState(),
            contentPadding = PaddingValues(15.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (categoryState) {
                is CategoryUiState.Loading -> {
                    items(4) {
                        SkeletonGridCell(shape = shape)
                    }
                }

                is CategoryUiState.Ready -> {
                    category.value = categoryState.category
                    val musicInfoList = categoryState.list
                    items(musicInfoList.size) { index ->
                        musicInfoList[index].let { musicInfo ->
                            GridCell(
                                shape = shape,
                                imageUrl = musicInfo.imageUrl,
                                title = musicInfo.title,
                                content = musicInfo.content,
                                onClick = { navigateToList(musicInfo) }
                            )
                        }
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
    onSeeAll: (String) -> Unit,
    categoryState: CategoryUiState,
) {
    val category = remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        CategoryTitle(
            title = title,
            onSeeAllClick = {
                onSeeAll(category.value)
            }
        )

        LazyHorizontalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            state = rememberLazyGridState(),
            rows = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                vertical = 8.dp,
                horizontal = 16.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (categoryState) {
                is CategoryUiState.Loading -> {
                    items(6) {
                        SkeletonGridMiniCell()
                    }
                }

                is CategoryUiState.Ready -> {
                    category.value = categoryState.category
                    val musicInfoList = categoryState.list
                    items(musicInfoList.size) { index ->
                        musicInfoList[index].let { musicInfo ->
                            GridMiniCell(
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
        modifier = modifier
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
            shape = RectangleShape,
            title = "Your top albums",
            navigateToList = {},
            onSeeAll = {},
            categoryState = CategoryUiState.Ready(
                category = "favorite",
                list = PreviewMusicInfoList
            )
        )
    }
}

@DevicePreviews
@Composable
fun PreviewMiniCategory() {
    SpotifyTheme {
        MiniCategory(
            title = "Your top albums",
            onSeeAll = {},
            categoryState = CategoryUiState.Ready(
                category = "favorite",
                list = PreviewMusicInfoList
            )
        )
    }
}