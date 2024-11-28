package com.litbig.spotify.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicInfoList
import com.litbig.spotify.ui.tooling.PreviewMusicInfoListFlow
import com.litbig.spotify.ui.tooling.PreviewMusicInfoPagingData
import com.litbig.spotify.util.ColorExtractor.getRandomPastelColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import timber.log.Timber

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToList: (MusicInfo) -> Unit
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onMore = viewModel::onMore,
        navigateToList = navigateToList,
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onMore: () -> Unit,
    navigateToList: (MusicInfo) -> Unit
) {
    val bgColor = remember { getRandomPastelColor() }

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
                    onSeeAll = onMore,
                    categoryState = uiState.favoriteState,
                )
            }
        }

        item {
            Category(
                navigateToList = navigateToList,
                title = "Your top albums",
                onSeeAll = onMore,
                categoryState = uiState.albumState
            )
        }

        item {
            Category(
                shape = CircleShape,
                navigateToList = navigateToList,
                title = "Your top artists",
                onSeeAll = onMore,
                categoryState = uiState.artistState
            )
        }
    }
}

@DevicePreviews
@Composable
fun PreviewHomeScreen() {
    SpotifyTheme {
        HomeScreen(
            uiState = HomeUiState(),
            onMore = {},
            navigateToList = {},
        )
    }
}