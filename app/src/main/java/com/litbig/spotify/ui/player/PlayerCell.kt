package com.litbig.spotify.ui.player

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.component.shimmerPainter
import com.litbig.spotify.ui.list.AnimatedEQDrawable
import com.litbig.spotify.ui.list.TitleAndContent
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews

@Composable
fun PlayerCell(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    imageUrl: String? = null,
    title: String,
    artist: String,
    isFavorite: Boolean,
    totalTime: String,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
) {
    Row(
        modifier = modifier
            .width(624.dp)
            .height(72.dp)
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(5.dp))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(30.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isPlaying) {
                AnimatedEQDrawable()
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
            modifier = Modifier
                .size(width = 360.dp, height = 50.dp),
            title = title,
            content = artist,
            isPlaying = isPlaying
        )

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

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            modifier = Modifier,
            text = totalTime,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@DevicePreviews
@Composable
fun PreviewPlayerCell() {
    SpotifyTheme {
        PlayerCell(
            isPlaying = false,
            imageUrl = "https://via.placeholder.com/150",
            title = "Title",
            artist = "Artist",
            isFavorite = true,
            totalTime = "3:00",
            onClick = {},
            onFavorite = {},
        )
    }
}