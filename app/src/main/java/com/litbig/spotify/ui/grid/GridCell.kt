@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.litbig.spotify.ui.grid

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.component.shimmerPainter
import com.litbig.spotify.core.design.extension.shimmer
import com.litbig.spotify.ui.LocalNavAnimatedVisibilityScope
import com.litbig.spotify.ui.LocalSharedTransitionScope
import com.litbig.spotify.ui.imageBoundsTransform
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicInfo

@Composable
fun GridCell(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    imageUrl: String? = null,
    title: String,
    content: String,
    isPlayable: Boolean = false,
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
        val isPreview = LocalInspectionMode.current
        val cardModifier = if (!isPreview) {
            val sharedTransitionScope = LocalSharedTransitionScope.current
                ?: throw IllegalStateException("No Scope found")
            val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                ?: throw IllegalStateException("No animatedVisibilityScope found")
            with(sharedTransitionScope) {
                Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState("image-$title"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    boundsTransform = imageBoundsTransform
                )
            }
        } else Modifier

        Card(
            modifier = Modifier
                .padding(top = 20.dp)
                .size(182.dp)
                .then(cardModifier),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = shape,
        ) {
            Box {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = imageUrl,
                    contentDescription = "Grid Thumbnail",
                    contentScale = ContentScale.Crop,
                    placeholder = shimmerPainter(),
                    error = painterResource(id = R.drawable.baseline_image_not_supported_24),
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
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SkeletonGridCell(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
) {
    Column(
        modifier = modifier
            .size(width = 224.dp, height = 324.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier
                .padding(top = 20.dp)
                .size(182.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = shape,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmer()
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 25.dp)
                .size(width = 182.dp, height = 20.dp)
                .shimmer(),
        )

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(width = 182.dp, height = 18.dp)
                .shimmer(),
        )
    }
}

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
        verticalAlignment = Alignment.CenterVertically
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
                placeholder = shimmerPainter(),
                error = painterResource(id = R.drawable.baseline_image_not_supported_24),
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

@Composable
fun SkeletonGridMiniCell(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .size(width = 426.dp, height = 82.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmer(),
            )
        }

        Spacer(modifier = Modifier.width(21.dp))

        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .padding(top = 16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(width = 200.dp, height = 20.dp)
                    .shimmer()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(width = 150.dp, height = 16.dp)
                    .shimmer()
            )
        }
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
            start = Offset(0f, size.height - 40),
            end = Offset(0f, size.height - 15),
            strokeWidth = 9.dp.toPx()
        )
        drawLine(
            color = color,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
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
            content = "Canyon City, Crooked Still, Gregory Alan, Isakov, The Paper Kites",
            isPlayable = true,
            onClick = {}
        )
    }
}

@DevicePreviews
@Composable
fun SkeletonGridCellPreview() {
    SpotifyTheme {
        SkeletonGridCell()
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

@DevicePreviews
@Composable
fun PreviewSkeletonGridMiniCell() {
    SpotifyTheme {
        SkeletonGridMiniCell()
    }
}