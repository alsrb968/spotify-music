package com.litbig.spotify.ui.player.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.core.domain.model.remote.TrackDetails
import com.litbig.spotify.ui.player.PlayerViewModel
import com.litbig.spotify.ui.player.SquareCard

@Composable
fun TrackDetailsInfoCard(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val trackDetails by viewModel.trackDetailInfo.collectAsStateWithLifecycle(null)

    TrackDetailsInfoCard(
        modifier = modifier,
        trackDetails = trackDetails
    )
}

@Composable
fun TrackDetailsInfoCard(
    modifier: Modifier = Modifier,
    trackDetails: TrackDetails?
) {
    SquareCard(
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            state = rememberLazyListState(),
        ) {
            item {
                Text(
                    text = "크레딧",
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            val artistSize = trackDetails?.artists?.size ?: 0
            items(artistSize) { index ->
                val artist = trackDetails?.artists?.get(index)
                Text(
                    text = "${artist?.name}",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = "메인 아티스트",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
