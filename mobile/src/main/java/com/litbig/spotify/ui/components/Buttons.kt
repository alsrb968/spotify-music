package com.litbig.spotify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews

@Composable
fun FollowButton(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    isFollowed: Boolean,
    activatedBorderColor: Color = MaterialTheme.colorScheme.onSurface,
    deactivatedBorderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit,
) {
    val borderColor = if (isFollowed) activatedBorderColor else deactivatedBorderColor

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
            text = if (isFollowed) "팔로잉" else "팔로우하기",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@DevicePreviews
@Composable
fun FollowButtonPreview() {
    SpotifyTheme {
        Column {
            FollowButton(
                isFollowed = false,
                onClick = {}
            )

            FollowButton(
                shape = RoundedCornerShape(4.dp),
                isFollowed = true,
                onClick = {}
            )
        }
    }
}