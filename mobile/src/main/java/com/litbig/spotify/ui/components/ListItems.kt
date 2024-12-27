package com.litbig.spotify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.*

@Composable
fun ListTitle(
    modifier: Modifier = Modifier,
    title: String,
    onMore: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            onMore?.let {
                Text(
                    modifier = Modifier
                        .clickableScaled { onMore() },
                    text = stringResource(R.string.show_all),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        content()
    }
}

@Composable
fun ArtistListTitle(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    placeholder: Painter = rememberVectorPainter(image = Icons.Default.Album),
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                model = imageUrl,
                contentDescription = "Thumbnail",
                placeholder = placeholder,
                error = rememberVectorPainter(image = Icons.Default.Error),
            )

            Column(
                modifier = Modifier,
            ) {
                Text(
                    modifier = Modifier,
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    modifier = Modifier,
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        content()
    }
}

@Composable
fun ListItemVerticalMedium(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    imageSize: Dp = 90.dp,
    shape: Shape = RoundedCornerShape(4.dp),
    title: String,
    subtitle: String? = null,
    placeholder: Painter = rememberVectorPainter(image = Icons.Default.Album),
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableScaled { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(imageSize)
                .clip(shape),
            model = imageUrl,
            contentDescription = "Thumbnail",
            placeholder = placeholder,
            error = rememberVectorPainter(image = Icons.Default.Error),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )

            subtitle?.let {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun ListItemHorizontalMedium(
    modifier: Modifier = Modifier,
    size: Dp = 145.dp,
    imageUrl: String?,
    shape: Shape = RectangleShape,
    title: String,
    subtitle: String? = null,
    placeholder: Painter = rememberVectorPainter(image = Icons.Default.Album),
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .height(200.dp)
            .clickableScaled { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(size)
                .clip(shape),
            model = imageUrl,
            contentDescription = "Thumbnail",
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = rememberVectorPainter(image = Icons.Default.Error),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier
                .widthIn(max = size),
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
        )

        subtitle?.let {
            Text(
                modifier = Modifier
                    .widthIn(max = size),
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun MenuIconItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@DevicePreviews
@Composable
private fun ListTitlePreview() {
    SpotifyTheme {
        ListTitle(
            title = PreviewArtistUiModel.name,
            onMore = {},
            content = {}
        )
    }
}

@DevicePreviews
@Composable
private fun ArtistListTitlePreview() {
    SpotifyTheme {
        ArtistListTitle(
            imageUrl = null,
            placeholder = PreviewAlbumArt(),
            title = PreviewArtistUiModel.name,
            subtitle = "팬들을 위한 음악"
        )
    }
}

@DevicePreviews
@Composable
private fun ListItemVerticalMediumPreview() {
    SpotifyTheme {
        ListItemVerticalMedium(
            imageUrl = null,
            title = PreviewTrackUiModel.name,
            subtitle = PreviewTrackUiModel.artists,
            placeholder = PreviewAlbumArt(),
            onClick = {}
        )
    }
}

@DevicePreviews
@Composable
private fun ListItemHorizontalMediumPreview() {
    SpotifyTheme {
        ListItemHorizontalMedium(
            imageUrl = null,
            title = PreviewTrackUiModel.name + PreviewTrackUiModel.name + PreviewTrackUiModel.name,
            subtitle = PreviewTrackUiModel.artists,
            placeholder = PreviewAlbumArt(),
            onClick = {}
        )
    }
}

@DevicePreviews
@Composable
private fun MenuIconItemPreview() {
    SpotifyTheme {
        MenuIconItem(
            imageVector = Icons.Default.Album,
            title = "Album",
        )
    }
}