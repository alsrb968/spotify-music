package com.litbig.spotify.ui.player.cards

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.model.remote.ImageInfo
import com.litbig.spotify.ui.player.PlayerViewModel
import com.litbig.spotify.ui.player.SquareCard
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews

@Composable
fun ArtistDetailsInfoCard(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val artistDetails by viewModel.artistDetailInfo.collectAsStateWithLifecycle(null)

    ArtistDetailsInfoCard(
        modifier = modifier,
        artistDetails = artistDetails
    )
}

@Composable
fun ArtistDetailsInfoCard(
    modifier: Modifier = Modifier,
    artistDetails: ArtistDetails?
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
                    model = artistDetails?.images?.firstOrNull()?.url,
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
                Text(
                    text = artistDetails?.name ?: "아티스트 이름",
                    style = MaterialTheme.typography.titleLarge
                )

                val follower = artistDetails?.followers?.total ?: 0
                val formattedFollower = "월별 청취자 " +
                    if (follower > 10000) {
                    "%.1f만명".format(follower / 10000f)
                } else {
                    "${follower}명"
                }

                Text(
                    text = formattedFollower,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                val popularity = artistDetails?.popularity ?: 0
                val formattedPopularity = "인기도 $popularity"

                Text(
                    text = formattedPopularity,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                val genres = artistDetails?.genres?.joinToString(", ") ?: ""

                Text(
                    text = genres,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@DevicePreviews
@Composable
fun PreviewArtistDetailInfo() {
    SpotifyTheme {
        ArtistDetailsInfoCard(
            artistDetails = ArtistDetails(
                externalUrls = com.litbig.spotify.core.domain.model.remote.ExternalUrls(
                    spotify = "https://open.spotify.com/artist/1uNFoZAHBGtllmzznpCI3s"
                ),
                followers = com.litbig.spotify.core.domain.model.remote.Followers(
                    href = null,
                    total = 1000000
                ),
                genres = listOf("Pop", "Hip-Hop"),
                href = "https://api.spotify.com/v1/artists/1uNFoZAHBGtllmzznpCI3s",
                id = "1uNFoZAHBGtllmzznpCI3s",
                images = listOf(
                    ImageInfo(
                        height = 640,
                        url = "https://i.scdn",
                        width = 640,
                    )
                ),
                name = "Justin Bieber",
                popularity = 100,
                type = "artist",
                uri = "spotify:artist:1uNFoZAHBGtllmzznpCI3s"
            )
        )
    }
}
