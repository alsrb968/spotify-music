package com.litbig.spotify.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.ui.home.category.Category
import com.litbig.spotify.ui.home.category.MiniCategory
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.util.ColorExtractor.getRandomPastelColor

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToGrid: (String) -> Unit,
    navigateToList: (MusicInfo) -> Unit
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onMore = navigateToGrid,
        navigateToList = navigateToList,
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onMore: (String) -> Unit,
    navigateToList: (MusicInfo) -> Unit
) {
    val bgColor = remember { getRandomPastelColor() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
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