package com.litbig.spotify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

val COLLAPSED_TOP_BAR_HEIGHT = 90.dp
val EXPANDED_TOP_BAR_HEIGHT = 400.dp

@Composable
fun ExpandedTopBar(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    dominantColor: Color = MaterialTheme.colorScheme.background,
    scrollProgress: Float = 0f
) {
    Box(
        modifier = modifier
            .background(dominantColor)
            .fillMaxWidth()
            .height(EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT),
        contentAlignment = Alignment.BottomStart
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .alpha(scrollProgress)
                .scale(1.0f + scrollProgress * 0.1f),
            model = imageUrl,
            contentDescription = "Album Art",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            placeholder = rememberVectorPainter(image = Icons.Default.Album),
            error = rememberVectorPainter(image = Icons.Default.Error)
        )
    }
}

@Composable
fun CollapsedTopBar(
    modifier: Modifier = Modifier,
    albumName: String,
    dominantColor: Color = MaterialTheme.colorScheme.background,
    progress: Float
) {
    Box(
        modifier = modifier
            .background(dominantColor.copy(alpha = progress))
            .fillMaxWidth()
            .height(COLLAPSED_TOP_BAR_HEIGHT)
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        // 텍스트 애니메이션
        Text(
            modifier = Modifier
                .padding(start = 60.dp, bottom = 4.dp)
                .graphicsLayer {
                    alpha = progress.coerceIn(0f, 1f)
                    translationY = (1f - progress) * 20f // 위로 이동
                },
            text = albumName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}