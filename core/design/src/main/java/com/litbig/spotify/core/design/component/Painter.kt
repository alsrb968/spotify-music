package com.litbig.spotify.core.design.component

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun shimmerPainter(): Painter {
    val shimmerTransition = rememberInfiniteTransition(label = "")

    // Shimmer 애니메이션: 왼쪽에서 오른쪽으로 이동
    val shimmerX by shimmerTransition.animateFloat(
        initialValue = -200f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    return object : Painter() {
        override val intrinsicSize: Size
            get() = Size.Unspecified

        override fun DrawScope.onDraw() {
            val shimmerBrush = Brush.linearGradient(
                colors = listOf(
                    Color.Gray.copy(alpha = 0.6f),
                    Color.LightGray.copy(alpha = 0.9f),
                    Color.Gray.copy(alpha = 0.6f)
                ),
                start = Offset(shimmerX, 0f),
                end = Offset(shimmerX + 200f, size.height)
            )
            drawRect(brush = shimmerBrush, size = size)
        }
    }
}