package com.litbig.spotify.ui.home.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.core.design.extension.extractDominantColorFromUrl
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.ui.components.*
import com.litbig.spotify.ui.models.AlbumUiModel
import com.litbig.spotify.ui.models.ArtistUiModel
import com.litbig.spotify.ui.models.PlaylistUiModel
import com.litbig.spotify.ui.models.TrackUiModel
import com.litbig.spotify.ui.shared.Loading
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ArtistDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is ArtistDetailUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is ArtistDetailUiState.Ready -> {

            val context = LocalContext.current
            LaunchedEffect(Unit) {
                val dominantColor = extractDominantColorFromUrl(context, s.artist.imageUrl)
                viewModel.sendIntent(ArtistDetailUiIntent.SetDominantColor(dominantColor))
            }

            ArtistDetailScreen(
                modifier = modifier,
                artist = s.artist,
                albums = s.albums,
                topTracks = s.topTracks,
                playlists = s.playlists,
                navigateBack = navigateBack,
            )
        }
    }
}

@Composable
fun ArtistDetailScreen(
    modifier: Modifier = Modifier,
    artist: ArtistUiModel,
    albums: List<AlbumUiModel>,
    topTracks: List<TrackUiModel>,
    playlists: List<PlaylistUiModel>,
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
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .clickableScaled { /* todo */ },
                            text = buildTracksInfo(topTracks),
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 32.sp),
                        )

                        playlists.firstOrNull()?.let { playlist ->
                            RepresentativePlaylist(
                                artistImageUrl = artist.imageUrl,
                                albumImageUrl = playlist.imageUrl,
                                description = playlist.description,
                                title = playlist.name,
                                subTitle = "플레이리스트",
                                onClick = { /* todo */ }
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ListTitle(
                                title = "인기음악",
                                onMore = { /* todo */ }
                            )

                            albums.take(4).forEachIndexed { index, album ->
                                ListItemVerticalMedium(
                                    imageUrl = album.imageUrl,
                                    title = album.name,
                                    subtitle = album.artists,
                                    onClick = { /* todo */ }
                                )
                            }

                            BorderButton(
                                modifier = Modifier
                                    .width(180.dp),
                                textInactive = "디스코그래피 보기",
                                onClick = { /* todo */ }
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            ListTitle(title = "상세정보")
                            ArtistDetailInfo(
                                artist = artist,
                                onClick = { /* todo */ }
                            )
                        }

                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(150.dp))
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
            BorderButton(
                shape = RoundedCornerShape(4.dp),
                isActive = false,
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

@Composable
fun ArtistDetailInfo(
    modifier: Modifier = Modifier,
    artist: ArtistUiModel,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickableScaled { onClick() }
    ) {
        SquareSurface(
            modifier = Modifier
                .padding(top = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(4.dp)),
        ) {
            Box(
                modifier = Modifier,
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize(),
                    model = artist.imageUrl,
                    contentDescription = "Artist Image",
                    contentScale = ContentScale.Crop,
                    placeholder = rememberVectorPainter(image = Icons.Default.Person),
                    error = rememberVectorPainter(image = Icons.Default.Error),
                )

                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(32.dp),
                        imageVector = Icons.Filled.Verified,
                        contentDescription = "Verified",
                        tint = Color(0xFF2e77d0)
                    )

                    Text(
                        text = "인증된 아티스트",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(MaterialTheme.typography.headlineSmall.toSpanStyle()) {
                                val format = NumberFormat.getNumberInstance(Locale.US).format(artist.follower)
                                append(format.toString())
                            }
                            append(" ")
                            withStyle(MaterialTheme.typography.labelSmall.toSpanStyle()) {
                                append("월별 리스너")
                            }
                        },
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1f),
                            text = PreviewLoremIpsum,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 18.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopEnd)
                .background(
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = CircleShape
                ),
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = buildAnnotatedString {
                    withStyle(MaterialTheme.typography.headlineSmall.toSpanStyle()) {
                        append(artist.popularity.toString())
                    }
                    append("\n")
                    withStyle(MaterialTheme.typography.labelSmall.toSpanStyle()) {
                        append("전 세계")
                    }
                },
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.background,
                lineHeight = 12.sp
            )
        }
    }
}

@DevicePreviews
@Composable
private fun ArtistDetailScreenPreview() {
    SpotifyTheme {
        ArtistDetailScreen(
            artist = PreviewArtistUiModel,
            albums = PreviewAlbumUiModels,
            topTracks = PreviewTrackUiModels,
            playlists = PreviewPlaylistUiModels,
            navigateBack = { }
        )
    }
}