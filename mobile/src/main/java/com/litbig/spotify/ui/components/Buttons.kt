package com.litbig.spotify.ui.components

import android.graphics.drawable.Icon
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BorderButton(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    isActive: Boolean = false,
    textActive: String = "팔로잉",
    textInactive: String = "팔로우하기",
    activatedBorderColor: Color = MaterialTheme.colorScheme.onSurface,
    inactivatedBorderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit,
) {
    val borderColor = if (isActive) activatedBorderColor else inactivatedBorderColor

    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickableScaled { onClick() },
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = if (isActive) textActive else textInactive,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ScalableIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Scale 애니메이션 처리
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "IconButtonScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = tween(100),
        label = "IconButtonAlpha"
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .padding(4.dp)
            .scale(scale) // Scale 애니메이션 적용
            .alpha(alpha) // Pressed 상태일 때 투명도 조절
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Ripple 제거
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit,
) {
    ScalableIconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.CheckCircle else Icons.Outlined.AddCircleOutline,
            contentDescription = "Favorite Button",
            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@DevicePreviews
@Composable
private fun BorderButtonPreview() {
    SpotifyTheme {
        Column {
            BorderButton(
                isActive = false,
                onClick = {}
            )

            BorderButton(
                shape = RoundedCornerShape(4.dp),
                isActive = true,
                onClick = {}
            )
        }
    }
}

@DevicePreviews
@Composable
private fun FavoriteButtonPreview() {
    SpotifyTheme {
        Column {
            FavoriteButton(
                isFavorite = false,
                onClick = {}
            )

            FavoriteButton(
                isFavorite = true,
                onClick = {}
            )
        }
    }
}