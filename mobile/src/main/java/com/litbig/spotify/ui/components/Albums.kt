package com.litbig.spotify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.ui.home.feed.FeedAlbum
import com.litbig.spotify.ui.home.feed.FeedCollection
import com.litbig.spotify.ui.theme.SpotifyTheme

@Composable
fun AlbumCollection(
    modifier: Modifier = Modifier,
    feedCollection: FeedCollection,
    onAlbum: (String) -> Unit,
    onMore:() -> Unit
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
                modifier = Modifier.clickable { onMore() }
            )
        }

        Albums(
            modifier = Modifier.fillMaxWidth(),
            feeds = feedCollection.feeds,
            onClick = onAlbum
        )
    }
}

@Composable
fun Albums(
    modifier: Modifier = Modifier,
    feeds: List<FeedAlbum>,
    onClick: (String) -> Unit
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(feeds.size) { index ->
            val feed = feeds[index]
            AlbumItem(
                imageUrl = feed.imageUrl,
                text = feed.name,
                onClick = { onClick(feed.id) }
            )
        }
    }
}

@Composable
fun AlbumItem(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .width(154.dp),
    ) {
        Box(
            modifier = Modifier
                .size(154.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() },
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = imageUrl,
                contentDescription = "Album Image",
                contentScale = ContentScale.Crop,
                placeholder = rememberVectorPainter(image = Icons.Default.Album),
                error = rememberVectorPainter(image = Icons.Default.Error),
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun AlbumCollectionPreview() {
    SpotifyTheme {
        AlbumCollection(
            feedCollection = FeedCollection(
                title = "Albums",
                feeds = emptyList()
            ),
            onAlbum = {},
            onMore = {}
        )
    }
}