package com.litbig.spotify.ui.grid

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicInfoPagingData
import kotlinx.coroutines.flow.Flow

@Composable
fun GridScreen(
    viewModel: GridViewModel = hiltViewModel(),
    navigateToList: (MusicInfo) -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    val title = when (state.value.category) {
        "favorite" -> "Your Favorites"
        "album" -> "Your top albums"
        "artist" -> "Your top artists"
        else -> ""
    }

    GridScreen(
        title = title,
        uiState = state.value,
        navigateToList = navigateToList
    )
}

@Composable
fun GridScreen(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    title: String,
    uiState: GridUiState,
    navigateToList: (MusicInfo) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
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
        }

        GridDisplay(
            shape = shape,
            pagingDataFlow = uiState.paging,
            navigateToList = navigateToList
        )
    }
}

@Composable
fun GridDisplay(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    pagingDataFlow: Flow<PagingData<MusicInfo>>,
    navigateToList: (MusicInfo) -> Unit,
) {
    val pagingItems = pagingDataFlow.collectAsLazyPagingItems()

    val isLoading = pagingItems.loadState.refresh is LoadState.Loading

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize(),
        state = rememberLazyGridState(),
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(15.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        if (isLoading) {
            items(8) {
                SkeletonGridCell(shape = shape)
            }
        } else {
            items(pagingItems.itemCount) { index ->
                val musicInfo = pagingItems[index] ?: return@items
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

@DevicePreviews
@Composable
fun PreviewGridScreen() {
    SpotifyTheme {
        GridScreen(
            title = "Your top albums",
            uiState = GridUiState(),
            navigateToList = {}
        )
    }
}

@DevicePreviews
@Composable
fun PreviewGridDisplay() {
    SpotifyTheme {
        GridDisplay(
            pagingDataFlow = PreviewMusicInfoPagingData,
            navigateToList = {}
        )
    }
}