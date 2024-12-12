@file:OptIn(ExperimentalMaterial3Api::class)

package com.litbig.spotify.ui.home.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.ui.components.AlbumCollection
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewFeedCollections
import timber.log.Timber

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
    onTrackSelected: (String) -> Unit,
    onAlbumSelected: (String) -> Unit,
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    FeedScreen(
        modifier = modifier,
        feedCollections = state.feedCollections,
        onTrackSelected = onTrackSelected,
        onAlbumSelected = onAlbumSelected
    )
}

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    feedCollections: List<FeedCollection>,
    onTrackSelected: (String) -> Unit,
    onAlbumSelected: (String) -> Unit
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier
            .padding(top = statusBarHeight),
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
                AlbumCollection(
                    feedCollection = feedCollection,
                    onTrack = onTrackSelected,
                    onAlbum = onAlbumSelected,
                    onMore = {
                        Timber.i("onMore")
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
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
fun PreviewFeedScreen() {
    SpotifyTheme {
        FeedScreen(
            feedCollections = PreviewFeedCollections,
            onTrackSelected = {},
            onAlbumSelected = {}
        )
    }
}