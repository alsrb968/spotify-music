package com.litbig.spotify.ui.home.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.ui.components.FeedCollection
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
    onShowSnackBar: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is FeedUiEffect.NavigateToAlbumDetail -> onAlbumSelected(effect.albumId)
                is FeedUiEffect.NavigateToArtistDetail -> Timber.i("NavigateToArtistDetail")
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
            MultipleFilterChipExample()
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

@Composable
fun MultipleFilterChipExample() {
    val filters = listOf("전체", "Wrapped 연말결산", "음악", "팟캐스트", "최근 재생한 항목")
    val selectedFilter = remember { mutableStateOf(filters.first()) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(filters.size) { index ->
            val filter = filters[index]
            val selected = selectedFilter.value == filter

            FilterChip(
                shape = CircleShape,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = Color.Transparent,
                ),
                label = {
                    Text(
                        text = filter,
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
                selected = selected,
                onClick = {
                    selectedFilter.value = filter
                },
            )
        }
    }
}

@DevicePreviews
@Composable
fun FeedScreenPreview() {
    SpotifyTheme {
        FeedScreen(
            feedCollections = PreviewFeedCollectionUiModels,
            onAlbum = {},
            onArtist = {},
            onMore = {},
        )
    }
}