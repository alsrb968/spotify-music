package com.litbig.spotify.ui.player.cards

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.ui.components.BorderButton
import com.litbig.spotify.ui.player.PlayerUiState
import com.litbig.spotify.ui.player.PlayerViewModel
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews

@Composable
fun TrackDetailsInfoCard(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is PlayerUiState.Idle -> {

        }

        is PlayerUiState.Ready -> {
            TrackDetailsInfoCard(
                modifier = modifier,
                artistNameList = s.artistNames
            )
        }
    }
}

@Composable
fun TrackDetailsInfoCard(
    modifier: Modifier = Modifier,
    artistNameList: List<String>,
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

            artistNameList.forEach { artistName ->
                Column {
                    RoleInfo(
                        artist = artistName,
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

        BorderButton(
            isActive = isFollowed,
            onClick = onClick
        )
    }
}

@DevicePreviews
@Composable
private fun RoleInfoPreview() {
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
private fun TrackDetailsInfoCardPreview() {
    SpotifyTheme {
        TrackDetailsInfoCard(
            artistNameList = List(3) { "Billie Eilish" }
        )
    }
}