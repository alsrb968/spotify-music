package com.litbig.spotify.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.component.shimmerPainter
import com.litbig.spotify.core.design.extension.shimmer
import com.litbig.spotify.core.domain.extension.toHumanReadableDuration
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicMetadata
import kotlinx.coroutines.delay

@Composable
fun ListCell(
    modifier: Modifier = Modifier,
    index: Int,
    isPlaying: Boolean = false,
    imageUrl: String? = null,
    title: String,
    artist: String,
    album: String,
    isFavorite: Boolean = false,
    totalTime: String,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(5.dp))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(20.dp))

        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isPlaying) {
                AnimatedEQDrawable()
            } else {
                Text(
                    text = index.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        AsyncImage(
            modifier = Modifier
                .size(52.dp),
            model = imageUrl,
            contentDescription = "List Thumbnail",
            contentScale = ContentScale.Crop,
            placeholder = shimmerPainter(),
            error = painterResource(id = R.drawable.baseline_image_not_supported_24)
        )

        Spacer(modifier = Modifier.width(20.dp))

        TitleAndContent(
            title = title,
            content = artist,
            isPlaying = isPlaying
        )

        Spacer(modifier = Modifier.width(45.dp))

        Text(
            modifier = Modifier
                .width(207.dp),
            text = album,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(215.dp))

        IconButton(
            modifier = Modifier,
            onClick = onFavorite,
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.width(30.dp))

        Text(
            modifier = Modifier,
            text = totalTime,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
fun SkeletonListCell(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(5.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(20.dp))

        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = 12.dp, height = 22.dp)
                    .shimmer()
            )
        }

        Box(
            modifier = Modifier
                .size(52.dp)
                .shimmer(),
        )

        Spacer(modifier = Modifier.width(20.dp))

        SkeletonTitleAndContent()

        Spacer(modifier = Modifier.width(45.dp))

        Box(
            modifier = Modifier
                .size(width = 207.dp, height = 18.dp)
                .shimmer(),
        )

        Spacer(modifier = Modifier.width(215.dp))

        IconButton(
            modifier = Modifier,
            onClick = {},
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.width(30.dp))

        Box(
            modifier = Modifier
                .size(width = 50.dp, height = 20.dp)
                .shimmer(),
        )
    }
}

@Composable
fun AnimatedEQDrawable(
    modifier: Modifier = Modifier
) {
    var currentState by remember { mutableIntStateOf(R.drawable.property_1_bars_1) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(300L)
            currentState = when (currentState) {
                R.drawable.property_1_bars_1 -> R.drawable.property_1_bars_2
                R.drawable.property_1_bars_2 -> R.drawable.property_1_bars_3
                R.drawable.property_1_bars_3 -> R.drawable.property_1_bars_4
                else -> R.drawable.property_1_bars_1
            }
        }
    }

    Image(
        modifier = modifier.size(28.dp),
        painter = painterResource(id = currentState),
        contentDescription = "Animated EQ"
    )
}

@Composable
fun TitleAndContent(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    isPlaying: Boolean,
) {
    Column(
        modifier = modifier
            .size(width = 227.dp, height = 50.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = if (isPlaying) Modifier
                .fillMaxWidth()
                .basicMarquee(
                    iterations = Int.MAX_VALUE,
                    repeatDelayMillis = 3000,
                )
            else Modifier,
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SkeletonTitleAndContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .size(width = 227.dp, height = 50.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(width = 150.dp, height = 20.dp)
                .shimmer(),
        )

        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 18.dp)
                .shimmer(),
        )
    }
}

@DevicePreviews
@Composable
private fun ListCellPreview() {
    SpotifyTheme {
        val item = PreviewMusicMetadata
        ListCell(
            index = 1,
            isPlaying = false,
            title = item.title,
            artist = item.artist,
            album = item.album,
            isFavorite = true,
            totalTime = item.duration.toHumanReadableDuration(),
            onClick = {},
            onFavorite = {},
        )
    }
}

@DevicePreviews
@Composable
private fun SkeletonListCellPreview() {
    SpotifyTheme {
        SkeletonListCell()
    }
}