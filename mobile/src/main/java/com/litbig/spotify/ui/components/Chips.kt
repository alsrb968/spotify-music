package com.litbig.spotify.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun SpotifyFilterChips(
    modifier: Modifier = Modifier,
    filters: List<String>,
    cancelable: Boolean = false,
    animDuration: Int = 300,
    initialSelectedFilter: String = "",
    onFilterSelected: (String) -> Unit,
) {
    val selectedFilter = remember { mutableStateOf(initialSelectedFilter) }

    var isVisibleClose by remember { mutableStateOf(false) }

    LaunchedEffect(selectedFilter.value) {
        isVisibleClose = selectedFilter.value.isNotEmpty()
        onFilterSelected(selectedFilter.value)
    }

    val enterAnim = fadeIn(animationSpec = tween(animDuration)) +
            expandHorizontally(
                animationSpec = tween(animDuration),
                expandFrom = Alignment.CenterHorizontally,
            )
    val exitAnim = fadeOut(animationSpec = tween(animDuration)) +
            shrinkHorizontally(
                animationSpec = tween(animDuration),
                shrinkTowards = Alignment.CenterHorizontally,
            )

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = animDuration)),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 8.dp),
    ) {
        if (cancelable) {
            item {
                AnimatedVisibility(
                    visible = isVisibleClose,
                    enter = enterAnim,
                    exit = exitAnim,
                ) {
                    SpotifyIconChip(
                        onClick = { selectedFilter.value = "" },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear filter",
                        )
                    }
                }
            }
        }

        items(filters.size) { index ->
            val filter = filters[index]
            val selected = selectedFilter.value == filter
            val isVisible = selectedFilter.value.isEmpty() || selected || !cancelable

            AnimatedVisibility(
                visible = isVisible,
                enter = enterAnim,
                exit = exitAnim,
            ) {
                SpotifyFilterChip(
                    modifier = Modifier
                        .padding(horizontal = 4.dp),
                    shape = CircleShape,
                    label = {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    selected = selected,
                    onClick = { selectedFilter.value = filter },
                    animDuration = animDuration,
                )
            }
        }
    }
}

@Composable
fun SpotifyFilterChip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = CircleShape,
    animDuration: Int = 300,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = animDuration),
        label = "backgroundColor"
    )

    // 애니메이션 텍스트 색상
    val textColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(durationMillis = animDuration),
        label = "textColor"
    )

    FilterChip(
        modifier = modifier,
        shape = shape,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = backgroundColor,
            labelColor = textColor,
            selectedContainerColor = backgroundColor,
            selectedLabelColor = textColor,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = Color.Transparent,
        ),
        leadingIcon = leadingIcon,
        label = label,
        trailingIcon = trailingIcon,
        selected = selected,
        enabled = enabled,
        onClick = onClick
    )
}

@Composable
fun SpotifyIconChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    shape: Shape = CircleShape,
    icon: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape) // 둥근 모양
            .background(MaterialTheme.colorScheme.surface) // 배경색
            .clickable {
                onClick()
            }
            .padding(4.dp) // 내부 여백
    ) {
        icon()
    }
}