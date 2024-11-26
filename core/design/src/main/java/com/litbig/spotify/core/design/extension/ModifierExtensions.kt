package com.litbig.spotify.core.design.extension

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

fun Modifier.gradientBackground(
    ratio: Float = 0.3f,
    startColor: Color,
    endColor: Color
): Modifier = composed {
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    this
        .onSizeChanged { boxSize = it }
        .background(
            brush = Brush.linearGradient(
                colors = listOf(startColor, endColor),
                start = Offset(0f, 0f),
                end = Offset(0f, boxSize.height * ratio)
            )
        )
}

fun Modifier.shimmer(): Modifier = composed {
    val shimmerTransition = rememberInfiniteTransition(label = "")

    // Shimmer 애니메이션: 왼쪽 상단에서 오른쪽 하단으로 이동
    val shimmerX by shimmerTransition.animateFloat(
        initialValue = -200f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    this.background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Gray.copy(alpha = 0.6f),
                Color.LightGray.copy(alpha = 0.9f),
                Color.Gray.copy(alpha = 0.6f)
            ),
            start = Offset(shimmerX, shimmerX), // 사선 시작점
            end = Offset(shimmerX + 200f, shimmerX + 200f) // 사선 끝점
        )
    )
}