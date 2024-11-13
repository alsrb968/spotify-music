package com.litbig.spotify.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.litbig.spotify.R
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewMusicMetadataList

@Composable
fun ListHeader(
    modifier: Modifier = Modifier,
    metadataList: List<MusicMetadata>,
) {
    Row(
        modifier = modifier
            .size(width = 988.dp, height = 300.dp),
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
        ) {
            metadataList.firstOrNull()?.albumArt?.let {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Album Art",
                )
            } ?: Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Album Art",
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "PUBLIC PLAYLIST",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.height(146.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chill Mix",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Julia Wolf, ayokay, Khalid and more",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Made for ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "davedirect3",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .width(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .align(Alignment.Center)
                            .background(
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = CircleShape
                            ),
                    )
                }
                Text(
                    text = "34 songs, 2hr 01 min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@DevicePreviews
@Composable
fun ListHeaderPreview() {
    SpotifyTheme {
        ListHeader(
            metadataList = PreviewMusicMetadataList
        )
    }
}