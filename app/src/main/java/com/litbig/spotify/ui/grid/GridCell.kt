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
import androidx.compose.ui.graphics.Path
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
    album: String,
    isPlayable: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .size(width = 224.dp, height = 324.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
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

                LineOverlay(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = coreColor
                )

                Text(
                    modifier = Modifier
                        .width(180.dp)
                        .padding(16.dp)
                        .align(Alignment.BottomStart),
                    text = album,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (isPlayable) {
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

@Composable
fun LineOverlay(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
    ) {
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(0f, size.height - 40),
            end = androidx.compose.ui.geometry.Offset(0f, size.height - 15),
            strokeWidth = 9.dp.toPx()
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(0f, size.height),
            end = androidx.compose.ui.geometry.Offset(size.width, size.height),
            strokeWidth = 8.dp.toPx()
        )
    }
}

@Composable
fun WaveOverlay(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
//        val topPath = Path().apply {
//            moveTo(0f, size.height * 0.35f)
//            cubicTo(
//                size.width * 0.05f, size.height * 0.3f,
//                size.width * 0.1f, size.height * 0.01f,
//                size.width * 0.25f, size.height * 0.05f
//            )
//            cubicTo(
//                size.width * 0.3f, size.height * 0.05f,
//                size.width * 0.4f, size.height * 0.2f,
//                size.width * 0.6f, size.height * 0f
//            )
//            lineTo(0f, 0f)
//            close()
//        }

        val bottomPath = Path().apply {
            moveTo(0f, size.height * 0.75f)
            quadraticBezierTo(
                size.width * 0.25f, size.height * 0.5f,
                size.width * 0.5f, size.height * 0.7f
            )
            quadraticBezierTo(
                size.width * 0.75f, size.height * 0.85f,
                size.width, size.height * 0.8f
            )
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }

//        drawPath(
//            path = topPath,
//            color = color
//        )

        drawPath(
            path = bottomPath,
            color = color
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
            album = "Folk & Acoustic",
            isPlayable = true,
            onClick = {}
        )
    }
}