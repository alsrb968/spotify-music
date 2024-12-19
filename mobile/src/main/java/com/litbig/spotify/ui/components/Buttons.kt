package com.litbig.spotify.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews

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