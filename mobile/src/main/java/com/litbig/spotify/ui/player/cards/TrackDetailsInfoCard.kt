package com.litbig.spotify.ui.player.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.model.remote.TrackDetails
import com.litbig.spotify.ui.player.PlayerViewModel
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewArtistDetailsList

@Composable
fun TrackDetailsInfoCard(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val trackDetails by viewModel.trackDetailInfo.collectAsStateWithLifecycle(null)

    TrackDetailsInfoCard(
        modifier = modifier,
        artistDetailsList = trackDetails?.artists ?: emptyList()
    )
}

@Composable
fun TrackDetailsInfoCard(
    modifier: Modifier = Modifier,
    artistDetailsList: List<ArtistDetails>,
) {
    Card(
        modifier = modifier
            .padding(10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "크레딧",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    modifier = Modifier
                        .clickableScaled { /* todo */ },
                    text = "모두 표시",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            val isFollowed = remember { mutableStateOf(false) }

            artistDetailsList.forEach { artist ->
                Column {
                    RoleInfo(
                        artist = artist.name,
                        role = "메인 아티스트",
                        isFollowed = isFollowed.value,
                        onClick = {
                            isFollowed.value = !isFollowed.value
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun RoleInfo(
    modifier: Modifier = Modifier,
    artist: String,
    role: String,
    isFollowed: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableScaled(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = role,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val borderColor =
            if (isFollowed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant

        Box(
            modifier = Modifier
                .background(
                    color = Color.Transparent,
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = CircleShape
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clickableScaled { onClick() },

            ) {
            Text(
                text = if (isFollowed) "팔로잉" else "팔로우하기",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@DevicePreviews
@Composable
fun PreviewRoleInfo() {
    SpotifyTheme {
        RoleInfo(
            artist = "아티스트 이름",
            role = "메인 아티스트",
            isFollowed = false,
            onClick = {}
        )
    }
}

@DevicePreviews
@Composable
fun PreviewTrackDetailsInfoCard() {
    SpotifyTheme {
        TrackDetailsInfoCard(
            artistDetailsList = PreviewArtistDetailsList.take(3)
        )
    }
}