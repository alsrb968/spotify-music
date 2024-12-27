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
import com.litbig.spotify.ui.models.FeedCollectionType
import com.litbig.spotify.ui.models.FeedCollectionUiModel
import com.litbig.spotify.ui.models.FeedUiModel
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewFeedCollectionUiModel
import kotlinx.serialization.json.JsonNull.content

@Composable
fun FeedCollection(
    modifier: Modifier = Modifier,
    feedCollection: FeedCollectionUiModel,
    onAlbum: (String) -> Unit,
    onArtist: (String) -> Unit,
    onMore: () -> Unit
) {
    FeedCollection(
        modifier = modifier,
        feedCollection = feedCollection,
        onMore = onMore
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val feeds = feedCollection.feeds
            items(feeds.size) { index ->
                val feed = feeds[index]
                FeedItem(
                    shape = if (feed.type == "artist") CircleShape else RoundedCornerShape(16.dp),
                    feed = feed,
                    onClick = {
                        if (feed.type == "artist") onArtist(feed.id)
                        else onAlbum(feed.id)
                    }
                )
            }
        }
    }
}

@Composable
fun FeedCollection(
    modifier: Modifier = Modifier,
    feedCollection: FeedCollectionUiModel,
    onMore: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    if (feedCollection.titleType == FeedCollectionType.ALBUMS_OF_ARTISTS) {
        ArtistListTitle(
            modifier = modifier
                .padding(horizontal = 16.dp),
            imageUrl = feedCollection.imageUrl,
            title = feedCollection.title,
            subtitle = "팬들을 위한 음악",
        ) {
            content()
        }
    } else {
        ListTitle(
            modifier = modifier
                .padding(horizontal = 16.dp),
            title = feedCollection.title,
            onMore = { onMore() }
        ) {
            content()
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
    val size = 165.dp
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
private fun FeedCollectionPreview() {
    SpotifyTheme {
        FeedCollection(
            feedCollection = PreviewFeedCollectionUiModel,
            onAlbum = {},
            onArtist = {},
            onMore = {}
        )
    }
}