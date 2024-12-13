package com.litbig.spotify.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews

@Composable
fun TrackItem(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    isPlaying: Boolean = false,
    title: String,
    artist: String,
    isFavorite: Boolean = false,
    onClick: () -> Unit,
    onMore: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp)
            .clickableScaled { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Box(
            modifier = Modifier
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(48.dp)
                    .padding(),
                model = imageUrl,
                contentDescription = "List Thumbnail",
                contentScale = ContentScale.Crop,
                placeholder = rememberVectorPainter(image = Icons.Default.Album),
                error = rememberVectorPainter(image = Icons.Default.Error),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        TitleAndContent(
            title = title,
            content = artist,
            isPlaying = isPlaying
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isFavorite) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Favorite",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        IconButton(
            onClick = onMore
        ) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = "More",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
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
            .width(227.dp),
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
            style = MaterialTheme.typography.bodySmall,
            color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = content,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@DevicePreviews
@Composable
private fun TrackItemPreview() {
    SpotifyTheme {
        TrackItem(
            imageUrl = null,
            isPlaying = true,
            title = "Title",
            artist = "Artist",
            isFavorite = true,
            onClick = {},
            onMore = {}
        )
    }
}