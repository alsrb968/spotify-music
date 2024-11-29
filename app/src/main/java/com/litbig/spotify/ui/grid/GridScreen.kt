package com.litbig.spotify.ui.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews

@Composable
fun GridScreen(
    viewModel: GridViewModel = hiltViewModel(),
    navigateToList: (MusicInfo) -> Unit,
    navigateBack: () -> Unit,
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
        navigateToList = navigateToList,
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridScreen(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    title: String,
    uiState: GridUiState,
    navigateToList: (MusicInfo) -> Unit,
    navigateBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = Color.Transparent,
                )
            )
        }
    ) { paddingValues ->
        val pagingItems = uiState.paging.collectAsLazyPagingItems()

        val isLoading = pagingItems.loadState.refresh is LoadState.Loading

        LazyVerticalGrid(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
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
}

@DevicePreviews
@Composable
fun PreviewGridScreen() {
    SpotifyTheme {
        GridScreen(
            title = "Your top albums",
            uiState = GridUiState(),
            navigateToList = {},
            navigateBack = {},
        )
    }
}