package com.litbig.spotify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.litbig.spotify.core.design.extension.gradientBackground

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

@Composable
fun ScrollableTopBarSurface(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    header: @Composable (Modifier) -> Unit,
    imageUrl: String?,
    dominantColor: Color = Color.Transparent,
    title: String,
    contentHorizonPadding: Dp = 16.dp,
    contentSpaceBy: Dp = 32.dp,
    contentBottomPadding: Dp = 200.dp,
    content: @Composable () -> Unit,
) {
    val listState = rememberLazyListState()
    val scrollProgress by remember {
        derivedStateOf {
            val maxOffset = 600f // 희미해지기 시작하는 최대 오프셋 값
            val firstVisibleItem = listState.firstVisibleItemIndex
            val scrollOffset = listState.firstVisibleItemScrollOffset.toFloat()
            if (firstVisibleItem == 0) {
                1f - (scrollOffset / maxOffset).coerceIn(0f, 1f)
            } else 0f
        }
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        CollapsedTopBar(
            modifier = Modifier.zIndex(2f),
            albumName = title,
            dominantColor = dominantColor,
            progress = 1f - scrollProgress
        )
        ExpandedTopBar(
            imageUrl = imageUrl,
            dominantColor = dominantColor,
            scrollProgress = scrollProgress
        )

        IconButton(
            modifier = Modifier
                .zIndex(3f)
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 32.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            onClick = onBack,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Spacer(modifier = Modifier.height(EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT * 2))
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(COLLAPSED_TOP_BAR_HEIGHT)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            item {
                header(
                    Modifier
                        .gradientBackground(
                            ratio = 1f,
                            startColor = dominantColor,
                            endColor = MaterialTheme.colorScheme.background
                        )
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = contentHorizonPadding),
                        verticalArrangement = Arrangement.spacedBy(contentSpaceBy)
                    ) {
                        content()
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(contentBottomPadding))
            }
        }
    }
}