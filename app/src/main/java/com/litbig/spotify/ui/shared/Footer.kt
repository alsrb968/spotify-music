package com.litbig.spotify.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.litbig.spotify.R
import com.litbig.spotify.core.data.mapper.local.MusicMetadataMapper.toLong
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.util.ConvertExtensions.toHumanReadableDuration
import com.litbig.spotify.core.domain.model.MusicMetadata
import com.litbig.spotify.ui.tooling.PreviewMusicMetadata
import timber.log.Timber

@Composable
fun FooterCollapsed(
    modifier: Modifier = Modifier
) {

}

@Composable
fun FooterExpanded(
    modifier: Modifier = Modifier,
    musicMetadata: MusicMetadata,
    playingTime: Long,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    Timber.i("musicMetadata: $musicMetadata")

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
//        Box(
//            modifier = Modifier
//                .clickable { onClick() }
//        ) {
//            Box(
//                modifier = Modifier.size(310.dp)
//            ) {
//                musicMetadata.albumArt?.let {
//                    Image(
//                        modifier = Modifier.fillMaxSize(),
//                        bitmap = it,
//                        contentDescription = "Album Art",
//                    )
//                } ?: Image(
//                    modifier = Modifier.fillMaxSize(),
//                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
//                    contentDescription = "Album Art",
//                )
//            }
//            Image(
//                modifier = Modifier
//                    .size(30.dp)
//                    .offset(x = (-10).dp, y = 10.dp)
//                    .align(Alignment.TopEnd),
//                painter = painterResource(id = R.drawable.forward),
//                contentDescription = "Collapse Button",
//            )
//        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .background(color = MaterialTheme.colorScheme.surfaceContainer),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(18.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
            ) {
                musicMetadata.albumArt?.let {
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

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                modifier = Modifier
                    .widthIn(max = 150.dp)
                    .height(43.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = musicMetadata.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = musicMetadata.artist,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                modifier = Modifier,
                onClick = { /*TODO*/ },
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            ControlBar(
                playingTime = playingTime,
                totalTime = musicMetadata.duration.toLong(),
            )
        }
    }
}

@Composable
fun ControlBar(
    modifier: Modifier = Modifier,
    playingTime: Long,
    totalTime: Long,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Image(
                modifier = Modifier
                    .size(32.dp),
                painter = painterResource(id = R.drawable.shuffle_s),
                contentDescription = "Shuffle Button",
            )

            Spacer(modifier = Modifier.width(22.dp))

            Image(
                modifier = Modifier
                    .size(32.dp),
                painter = painterResource(id = R.drawable.property_1_prev_s),
                contentDescription = "Previous Button",
            )

            Spacer(modifier = Modifier.width(22.dp))

            Image(
                modifier = Modifier
                    .size(48.dp),
                painter = painterResource(id = R.drawable.property_1_pause),
                contentDescription = "Play/Pause Button",
            )

            Spacer(modifier = Modifier.width(22.dp))

            Image(
                modifier = Modifier
                    .size(32.dp),
                painter = painterResource(id = R.drawable.property_1_next_s),
                contentDescription = "Next Button",
            )

            Spacer(modifier = Modifier.width(22.dp))

            Image(
                modifier = Modifier
                    .size(32.dp),
                painter = painterResource(id = R.drawable.repeat_s),
                contentDescription = "Repeat Button",
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(
                text = playingTime.toHumanReadableDuration(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.width(8.dp))

            RoundedMusicProgressBar(
                modifier = Modifier
                    .width(250.dp),
                progress = playingTime.toFloat() / totalTime.toFloat(),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = totalTime.toHumanReadableDuration(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

        }
    }
}

@Composable
fun RoundedMusicProgressBar(
    modifier: Modifier = Modifier,
    progress: Float,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(color = MaterialTheme.colorScheme.onSurface)
        )
    }
}

@DevicePreviews
@Composable
fun PreviewFooterExpanded() {
    SpotifyTheme {
        FooterExpanded(
            musicMetadata = PreviewMusicMetadata,
            playingTime = 159000,
            isFavorite = true,
            onClick = {}
        )
    }
}

@DevicePreviews
@Composable
fun PreviewControlBar() {
    SpotifyTheme {
        ControlBar(
            playingTime = 159000,
            totalTime = 262000,
        )
    }
}