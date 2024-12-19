package com.litbig.spotify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews

@Composable
fun RepresentativePlaylist(
    modifier: Modifier = Modifier,
    artistImageUrl: String?,
    albumImageUrl: String?,
    description: String,
    title: String,
    subTitle: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ListTitle(title = "아티스트 추천")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickableScaled { onClick() },
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = artistImageUrl,
                contentDescription = "Artist Thumbnail",
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                placeholder = rememberVectorPainter(image = Icons.Default.Album),
                error = rememberVectorPainter(image = Icons.Default.Error),
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                    blendMode = BlendMode.Multiply,
                )
            )

            if (description.isNotEmpty()) {
                ChipDescription(
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            start = 16.dp
                        ),
                    imageUrl = artistImageUrl,
                    description = description,
                )
            }

            RepresentativeAlbumInfo(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        bottom = 16.dp,
                        start = 16.dp
                    ),
                imageUrl = albumImageUrl,
                title = title,
                subTitle = subTitle,
            )
        }
    }
}

@Composable
fun ChipDescription(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    description: String,
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(24.dp)
                .padding(2.dp)
                .clip(shape = CircleShape),
            model = imageUrl,
            contentDescription = "Chip Thumbnail",
            contentScale = ContentScale.Crop,
            placeholder = rememberVectorPainter(image = Icons.Default.Album),
            error = rememberVectorPainter(image = Icons.Default.Error),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            modifier = Modifier
                .widthIn(max = 230.dp),
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.surface,
            maxLines = 1,
        )

        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
fun RepresentativeAlbumInfo(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    title: String,
    subTitle: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(48.dp),
            model = imageUrl,
            contentDescription = "Album Thumbnail",
            contentScale = ContentScale.Crop,
            placeholder = rememberVectorPainter(image = Icons.Default.Album),
            error = rememberVectorPainter(image = Icons.Default.Error),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )

            Text(
                text = subTitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun RepresentativePlaylistPreview() {
    SpotifyTheme {
        RepresentativePlaylist(
            artistImageUrl = null,
            albumImageUrl = null,
            description = "‘IM THE DRAMA' OUT NOW \uD83D\uDC85 Tap the ⨁ to be the first to hear new Bebe Rexha songs as soon as they’re released.",
            title = "Bebe Rexha - I'm The Drama",
            subTitle = "플레이리스트",
            onClick = { }
        )
    }
}