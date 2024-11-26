package com.litbig.spotify.ui.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicInfoPagingData
import com.litbig.spotify.util.ColorExtractor.getRandomPastelColor
import kotlinx.coroutines.flow.Flow

@Composable
fun GridScreen(
    viewModel: GridViewModel = hiltViewModel(),
    navigateToList: (MusicInfo) -> Unit
) {
    GridScreen(
        navigateToList = navigateToList,
        favoritePagingFlow = viewModel.favoritesPagingFlow,
        albumsPagingFlow = viewModel.albumsPagingFlow,
        artistPagingFlow = viewModel.artistPagingFlow
    )
}

@Composable
fun GridScreen(
    modifier: Modifier = Modifier,
    favoritePagingFlow: Flow<PagingData<MusicInfo>>,
    albumsPagingFlow: Flow<PagingData<MusicInfo>>,
    artistPagingFlow: Flow<PagingData<MusicInfo>>,
    navigateToList: (MusicInfo) -> Unit
) {
    val bgColor = remember { getRandomPastelColor() }

    val favoritePagingItems = favoritePagingFlow.collectAsLazyPagingItems()
    val albumsPagingItems = albumsPagingFlow.collectAsLazyPagingItems()
    val artistPagingItems = artistPagingFlow.collectAsLazyPagingItems()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceDim),
        state = rememberLazyListState()
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .gradientBackground(
                        ratio = 1f,
                        startColor = bgColor,
                        endColor = MaterialTheme.colorScheme.surfaceDim
                    )
            ) {
                GridMiniCategory(
                    title = "Your favorites",
                    musicInfoPagingItems = favoritePagingItems,
                )

                Spacer(modifier = Modifier.height(26.dp))
            }
        }

        item {
            GridCategory(
                navigateToList = navigateToList,
                title = "Your top albums",
                musicInfoPagingItems = albumsPagingItems
            )

            Spacer(modifier = Modifier.height(26.dp))
        }

        item {
            GridCategory(
                shape = CircleShape,
                navigateToList = navigateToList,
                title = "Your top artists",
                musicInfoPagingItems = artistPagingItems
            )

            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}

@DevicePreviews
@Composable
fun PreviewGridScreen() {
    SpotifyTheme {
        GridScreen(
            navigateToList = {},
            favoritePagingFlow = PreviewMusicInfoPagingData,
            albumsPagingFlow = PreviewMusicInfoPagingData,
            artistPagingFlow = PreviewMusicInfoPagingData
        )
    }
}