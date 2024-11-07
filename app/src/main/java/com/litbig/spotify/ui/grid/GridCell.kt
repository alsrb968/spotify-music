package com.litbig.spotify.ui.grid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.litbig.spotify.R
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews

@Composable
fun GridCell(
    modifier: Modifier = Modifier,
    albumArt: ImageBitmap? = null,
    coreColor: Color = Color.Yellow,
    title: String,
    artist: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .size(width = 224.dp, height = 324.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .padding(top = 20.dp)
                .size(182.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(4.dp),
        ) {
            Box(modifier = Modifier) {
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

                Text(
                    modifier = Modifier
                        .width(160.dp)
                        .padding(16.dp)
                        .align(Alignment.BottomStart),
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    drawLine(
                        color = coreColor,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height - 95),
                        end = androidx.compose.ui.geometry.Offset(0f, size.height - 45),
                        strokeWidth = 9.dp.toPx()
                    )
                    drawLine(
                        color = coreColor,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                        strokeWidth = 8.dp.toPx()
                    )
                }

                Image(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false, radius = 24.dp)
                        ) { /* TODO */ }
                        .size(62.dp)
//                        .padding(4.dp)
                        .align(Alignment.BottomEnd)
                        .semantics { role = Role.Button }
//                        .shadow(
//                            elevation = 8.dp,
//                            shape = CircleShape,
//                            clip = false
//                        )
                    ,
                    painter = painterResource(id = R.drawable.play_green_hover),
                    contentDescription = "Play"
                )
            }
        }

        Text(
            modifier = Modifier
                .padding(top = 25.dp)
                .width(182.dp),
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(182.dp),
            text = artist,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@DevicePreviews
@Composable
fun GridCellPreview() {
    SpotifyTheme {
        GridCell(
            title = "Folk & Acoustic Mix 2021",
            artist = "Canyon City, Crooked Still, Gregory Alan, Isakov, The Paper Kites",
            onClick = {}
        )
    }
}