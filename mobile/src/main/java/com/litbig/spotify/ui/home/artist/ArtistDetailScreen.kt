package com.litbig.spotify.ui.home.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.ui.components.*
import com.litbig.spotify.ui.models.ArtistUiModel
import com.litbig.spotify.ui.models.TrackUiModel
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewArtistUiModel
import com.litbig.spotify.ui.tooling.PreviewTrackUiModels

@Composable
fun ArtistDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    onShowSnackBar: (String) -> Unit,
) {

}

@Composable
fun ArtistDetailScreen(
    modifier: Modifier = Modifier,
    artist: ArtistUiModel,
    topTracks: List<TrackUiModel>,
    navigateBack: () -> Unit,
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
            albumName = artist.name,
            dominantColor = artist.dominantColor,
            progress = 1f - scrollProgress
        )
        ExpandedTopBar(
            imageUrl = artist.imageUrl,
            dominantColor = artist.dominantColor,
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
            onClick = navigateBack,
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
                        text = artist.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            item {
                ArtistInfoTitle(
                    modifier = Modifier
                        .gradientBackground(
                            ratio = 1f,
                            startColor = artist.dominantColor,
                            endColor = MaterialTheme.colorScheme.background
                        ),
                    artist = artist,

                )
            }

            item {
                Text(
                    text = buildTracksInfo(topTracks),
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 32.sp),
                )
            }
        }
    }
}

@Composable
fun ArtistInfoTitle(
    modifier: Modifier = Modifier,
    artist: ArtistUiModel,
) {
    val follower = artist.follower
    val formattedFollower = "월별 청취자 " +
            when {
                follower >= 100_000_000 ->
                    "%.1f억명".format(follower / 100_000_000f)

                follower >= 10_000 ->
                    "%.1f만명".format(follower / 10_000f)

                else ->
                    "${follower}명"
            }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = formattedFollower,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FollowButton(
                shape = RoundedCornerShape(4.dp),
                isFollowed = false,
                onClick = { /* todo */ }
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = { /* todo */ }
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Option",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { /* todo */ }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.property_1_shuffle_on),
                    contentDescription = "Shuffle",
                    tint = Color.Unspecified
                )
            }

            FloatingActionButton(
                modifier = Modifier,
                shape = CircleShape,
                onClick = { /* todo */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    modifier = Modifier
                        .size(36.dp),
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun buildTracksInfo(
    tracks: List<TrackUiModel>,
): AnnotatedString {
    return buildAnnotatedString {
        tracks.forEachIndexed { index, track ->
            if (index == 5) {
                append(
                    AnnotatedString(
                        text = "더 보기",
                        spanStyle = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                )
                return@buildAnnotatedString
            }

            append(
                AnnotatedString(
                    text = track.artists,
                    spanStyle = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            )

            append(" ")

            append(
                AnnotatedString(
                    text = track.name,
                    spanStyle = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            )

            if (index != tracks.lastIndex) {
                append(
                    AnnotatedString(
                        text = " ∙ ",
                        spanStyle = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ArtistDetailScreenPreview() {
    SpotifyTheme {
        ArtistDetailScreen(
            artist = PreviewArtistUiModel,
            topTracks = PreviewTrackUiModels,
            navigateBack = { }
        )
    }
}