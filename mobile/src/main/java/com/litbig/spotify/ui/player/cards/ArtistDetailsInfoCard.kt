package com.litbig.spotify.ui.player.cards

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litbig.spotify.ui.components.FollowButton
import com.litbig.spotify.ui.models.ArtistUiModel
import com.litbig.spotify.ui.player.PlayerUiState
import com.litbig.spotify.ui.player.PlayerViewModel
import com.litbig.spotify.ui.player.SquareCard
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewArtistUiModel

@Composable
fun ArtistDetailsInfoCard(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is PlayerUiState.Idle -> {

        }

        is PlayerUiState.Ready -> {
            ArtistDetailsInfoCard(
                modifier = modifier,
                artist = s.artist,
                onFollow = {}
            )
        }
    }
}

@Composable
fun ArtistDetailsInfoCard(
    modifier: Modifier = Modifier,
    artist: ArtistUiModel,
    onFollow: () -> Unit,
) {
    SquareCard(
        modifier = modifier,
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1.5f)
            ) {

                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize(),
                    model = artist.imageUrl,
                    contentDescription = "Artist Image",
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.TopCenter,
                    placeholder = rememberVectorPainter(image = Icons.Default.Album),
                    error = rememberVectorPainter(image = Icons.Default.Error),
                )

                Text(
                    modifier = Modifier
                        .padding(16.dp),
                    text = "아티스트 상세정보",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = artist.name,
                            style = MaterialTheme.typography.titleLarge
                        )

                        val follower = artist.follower
                        val formattedFollower = "월별 청취자 " +
                                when {
                                    follower >= 100_000_000 ->
                                        "%.1f억명".format(follower / 100_000_000f)

                                    follower >= 10_000 ->
                                        "%.1f만명".format(follower / 10_000f)

                                    else ->
                                        "${follower}명"
                                }

                        Text(
                            text = formattedFollower,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val isFollowed = remember { mutableStateOf(false) }

                    FollowButton(
                        isFollowed = isFollowed.value,
                        onClick = {
                            isFollowed.value = !isFollowed.value
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val formattedPopularity = "인기도 ${artist.popularity}"

                Text(
                    text = formattedPopularity,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = artist.genres,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@DevicePreviews
@Composable
fun ArtistDetailInfoPreview() {
    SpotifyTheme {
        ArtistDetailsInfoCard(
            artist = PreviewArtistUiModel,
            onFollow = {}
        )
    }
}
