package com.litbig.spotify.ui.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicInfo

@Composable
fun GridMiniCell(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    title: String,
    content: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .size(width = 426.dp, height = 82.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            .clickable { onClick() },
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = imageUrl,
                contentDescription = "Grid Thumbnail",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                error = painterResource(id = R.drawable.ic_launcher_foreground),
            )
        }

        Spacer(modifier = Modifier.width(21.dp))

        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .padding(top = 16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@DevicePreviews
@Composable
fun PreviewGridMiniCell() {
    SpotifyTheme {
        GridMiniCell(
            title = PreviewMusicInfo.title,
            content = "노래",
            onClick = { }
        )
    }
}