package com.litbig.spotify.ui.list

import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.component.shimmerPainter
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.ui.shared.getSharedModifier
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicInfo
import com.litbig.spotify.ui.tooling.PreviewMusicMetadataList
import kotlin.time.Duration

@Composable
fun ListHeader(
    modifier: Modifier = Modifier,
    musicInfo: MusicInfo,
    metadataList: List<MusicMetadata>,
) {
    val title = musicInfo.title
    val artist = musicInfo.content
    val artists = metadataList.map { it.artist }.distinct().joinToString(separator = ", ")
    val count = metadataList.size
    val durations = metadataList.fold(Duration.ZERO) { acc, metadata -> acc + metadata.duration }
    val hours = durations.inWholeHours
    val minutes = durations.inWholeMinutes % 60

    Row(
        modifier = modifier
            .size(width = 988.dp, height = 230.dp),
    ) {
        Box(
            modifier = Modifier
                .size(230.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .then(getSharedModifier("image-$title")),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RectangleShape,
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize(),
                    model = musicInfo.imageUrl,
                    contentDescription = "Album Art",
                    contentScale = ContentScale.Crop,
                    placeholder = shimmerPainter(),
                    error = painterResource(id = R.drawable.baseline_image_not_supported_24),
                )
            }
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
                modifier = Modifier.height(126.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .basicMarquee(
                            iterations = Int.MAX_VALUE,
                            animationMode = MarqueeAnimationMode.Immediately,
                            repeatDelayMillis = 1000,
                            initialDelayMillis = 3000,
                        ),
                    text = title,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                )
            }

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
                Text(
                    text = " • %d songs, %dhr %02d min".format(count, hours, minutes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ListHeaderPreview() {
    SpotifyTheme {
        ListHeader(
            musicInfo = PreviewMusicInfo,
            metadataList = PreviewMusicMetadataList
        )
    }
}