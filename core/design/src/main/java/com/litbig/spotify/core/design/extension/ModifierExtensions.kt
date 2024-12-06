package com.litbig.spotify.core.design.extension

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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


val LocalClickAnimationConfig = staticCompositionLocalOf { ClickAnimationConfig() }

data class ClickAnimationConfig(
    val scaleDown: Float = 0.95f, // 클릭 시 크기 감소 비율
    val animationDuration: Int = 150 // 애니메이션 지속 시간 (ms)
)

fun Modifier.clickableScaled(onClick: () -> Unit): Modifier = composed {
    val config = LocalClickAnimationConfig.current
    var isPressed by remember { mutableStateOf(false) }

    // Scale 애니메이션 상태
    val scale by animateFloatAsState(
        targetValue = if (isPressed) config.scaleDown else 1f,
        animationSpec = tween(config.animationDuration),
        label = "Click Scale Animation"
    )

    this
        .scale(scale) // Scale 애니메이션 적용
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    // 눌렀을 때 눌림 애니메이션 시작
                    isPressed = true
                    try {
                        // 손을 뗄 때까지 기다림
                        awaitRelease()
                        // 클릭 처리
                        onClick()
                    } finally {
                        // 손을 뗀 후 눌림 애니메이션 종료
                        isPressed = false
                    }
                },
                // 스크롤 등의 이벤트 중에는 클릭 처리하지 않음
                onTap = {}
            )
        }
}