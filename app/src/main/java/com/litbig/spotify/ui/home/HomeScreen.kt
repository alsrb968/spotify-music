package com.litbig.spotify.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToList: (MusicInfo) -> Unit
) {
    HomeScreen(
        navigateToList = navigateToList,
        favoritePagingFlow = viewModel.favoritesPagingFlow,
        albumsPagingFlow = viewModel.albumsPagingFlow,
        artistPagingFlow = viewModel.artistPagingFlow
    )
}

@Composable
fun HomeScreen(
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
                MiniCategory(
                    title = "Your favorites",
                    musicInfoPagingItems = favoritePagingItems,
                )
            }
        }

        item {
            Category(
                navigateToList = navigateToList,
                title = "Your top albums",
                musicInfoPagingItems = albumsPagingItems
            )
        }

        item {
            Category(
                shape = CircleShape,
                navigateToList = navigateToList,
                title = "Your top artists",
                musicInfoPagingItems = artistPagingItems
            )
        }
    }
}

@DevicePreviews
@Composable
fun PreviewHomeScreen() {
    SpotifyTheme {
        HomeScreen(
            navigateToList = {},
            favoritePagingFlow = PreviewMusicInfoPagingData,
            albumsPagingFlow = PreviewMusicInfoPagingData,
            artistPagingFlow = PreviewMusicInfoPagingData
        )
    }
}