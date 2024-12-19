package com.litbig.spotify.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp

@Composable
fun SquareSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val size = constraints.maxWidth.coerceAtMost(constraints.maxHeight)
        val imageConstraints = constraints.copy(
            minWidth = size,
            maxWidth = size,
            minHeight = size,
            maxHeight = size
        )

        // 이미지 측정
        val placeable = measurables.first().measure(imageConstraints)

        layout(size, size) {
            // 이미지 배치
            placeable.placeRelative(0, 0)
        }
    }
}

@Composable
fun SquareCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SquareSurface(
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            content()
        }
    }
}