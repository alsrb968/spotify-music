package com.litbig.spotify.ui.home.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.ui.components.FeedCollection
import com.litbig.spotify.ui.components.SpotifyFilterChips
import com.litbig.spotify.ui.models.FeedCollectionUiModel
import com.litbig.spotify.ui.shared.Loading
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewFeedCollectionUiModels
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
    onAlbumSelected: (String) -> Unit,
    onArtistSelected: (String) -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is FeedUiEffect.NavigateToAlbumDetail -> onAlbumSelected(effect.albumId)
                is FeedUiEffect.NavigateToArtistDetail -> onArtistSelected(effect.artistId)
                is FeedUiEffect.ShowMore -> Timber.i("ShowMore")
                is FeedUiEffect.ShowToast -> onShowSnackBar(effect.message)
            }
        }
    }

    when (val s = state) {
        is FeedUiState.Loading -> {
            Loading(modifier = modifier.fillMaxSize())
        }

        is FeedUiState.Ready -> {
            FeedScreen(
                modifier = modifier,
                feedCollections = s.feedCollections,
                onAlbum = { albumId ->
                    viewModel.sendIntent(FeedUiIntent.SelectAlbum(albumId))
                },
                onArtist = { artistId ->
                    viewModel.sendIntent(FeedUiIntent.SelectArtist(artistId))
                },
                onMore = {
                    viewModel.sendIntent(FeedUiIntent.ShowMore(it))
                }
            )
        }
    }
}

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    feedCollections: List<FeedCollectionUiModel>,
    onAlbum: (String) -> Unit,
    onArtist: (String) -> Unit,
    onMore: (FeedCollectionUiModel) -> Unit,
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        modifier = modifier
            .padding(
                top = statusBarHeight,
                bottom = navigationBarHeight,
            ),
        topBar = {
            SpotifyFilterChips(
                modifier = Modifier
                    .fillMaxWidth(),
                filters = listOf("전체", "Wrapped 연말결산", "음악", "팟캐스트", "최근 재생한 항목"),
                initialSelectedFilter = "전체",
                onFilterSelected = { filter ->
                    Timber.i("Selected filter: $filter")
                }
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding),
        ) {
            items(feedCollections.size) { index ->
                val feedCollection = feedCollections[index]
                FeedCollection(
                    feedCollection = feedCollection,
                    onAlbum = onAlbum,
                    onArtist = onArtist,
                    onMore = {
                        onMore(feedCollection)
                    }
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun FeedScreenPreview() {
    SpotifyTheme {
        FeedScreen(
            feedCollections = PreviewFeedCollectionUiModels,
            onAlbum = {},
            onArtist = {},
            onMore = {},
        )
    }
}