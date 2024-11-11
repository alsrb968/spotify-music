package com.litbig.spotify.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.litbig.spotify.R
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import kotlinx.coroutines.delay

@Composable
fun ListCell(
    modifier: Modifier = Modifier,
    index: Int,
    isPlaying: Boolean = false,
    albumArt: ImageBitmap? = null,
    title: String,
    artist: String,
    album: String,
    isFavorite: Boolean = false,
    totalTime: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.1f))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
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

        Box(
            modifier = Modifier
                .size(52.dp)
        ) {
            albumArt?.let {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = it,
                    contentDescription = "Grid Thumbnail"
                )
            } ?: Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Grid Thumbnail"
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier
                .size(width = 227.dp, height = 50.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(37.dp))

        Text(
            modifier = Modifier
                .width(207.dp),
            text = album,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(211.dp))

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

@DevicePreviews
@Composable
fun ListCellPreview() {
    SpotifyTheme {
        ListCell(
            index = 1,
            isPlaying = false,
            title = "A Moment Apart",
            artist = "ODESZA",
            album = "A Moment Apart",
            isFavorite = true,
            totalTime = "3:54",
            onClick = {}
        )
    }
}