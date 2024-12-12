package com.litbig.spotify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.ui.home.feed.FeedCollectionUiModel
import com.litbig.spotify.ui.home.feed.FeedUiModel
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewFeedCollectionUiModel

@Composable
fun FeedCollection(
    modifier: Modifier = Modifier,
    feedCollection: FeedCollectionUiModel,
    onAlbum: (String) -> Unit,
    onMore: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = feedCollection.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = stringResource(R.string.show_all),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickableScaled { onMore() }
            )
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val feeds = feedCollection.feeds
            items(feeds.size) { index ->
                val feed = feeds[index]
                FeedItem(
                    shape = if (feed.type == "artist") CircleShape else RoundedCornerShape(16.dp),
                    feed = feed,
                    onClick = { onAlbum(feed.id) }
                )
            }
        }
    }
}

@Composable
fun FeedItem(
    modifier: Modifier = Modifier,
    feed: FeedUiModel,
    shape: Shape = RoundedCornerShape(16.dp),
    onClick: () -> Unit
) {
    val size = 180.dp
    Column(
        modifier = modifier
            .width(size)
            .clickableScaled { onClick() },
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape),
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = feed.imageUrl,
                contentDescription = feed.name,
                contentScale = ContentScale.Crop,
                placeholder = rememberVectorPainter(image = Icons.Default.Album),
                error = rememberVectorPainter(image = Icons.Default.Error),
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            text = feed.name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@DevicePreviews
@Composable
fun FeedCollectionPreview() {
    SpotifyTheme {
        FeedCollection(
            feedCollection = PreviewFeedCollectionUiModel,
            onAlbum = {},
            onMore = {}
        )
    }
}