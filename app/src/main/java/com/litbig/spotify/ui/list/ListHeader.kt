package com.litbig.spotify.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.litbig.spotify.R
import com.litbig.spotify.core.domain.model.MusicMetadata
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicMetadataList
import kotlin.time.Duration

@Composable
fun ListHeader(
    modifier: Modifier = Modifier,
    metadataList: List<MusicMetadata>,
) {
    val first = metadataList.firstOrNull()
    val title = first?.album ?: ""
    val artist = first?.artistName ?: ""
    val artists = metadataList.map { it.artist }.distinct().joinToString(separator = ", ")
    val count = metadataList.size
    val durations = metadataList.fold(Duration.ZERO) { acc, metadata -> acc + metadata.duration }
    val hours = durations.inWholeHours
    val minutes = durations.inWholeMinutes % 60

    Row(
        modifier = modifier
            .size(width = 988.dp, height = 300.dp),
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
        ) {
            metadataList.firstOrNull()?.albumArt?.let {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Album Art",
                )
            } ?: Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Album Art",
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "PUBLIC PLAYLIST",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.height(146.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .basicMarquee(),
                    text = title,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = artists,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Made for ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = artist,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .width(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .align(Alignment.Center)
                            .background(
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = CircleShape
                            ),
                    )
                }
                Text(
                    text = "%d songs, %dhr %02d min".format(count, hours, minutes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@DevicePreviews
@Composable
fun ListHeaderPreview() {
    SpotifyTheme {
        ListHeader(
            metadataList = PreviewMusicMetadataList
        )
    }
}