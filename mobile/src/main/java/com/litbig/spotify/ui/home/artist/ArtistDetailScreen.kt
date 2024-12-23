package com.litbig.spotify.ui.home.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.core.design.extension.extractDominantColorFromUrl
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
    navigateToAlbum: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    navigateToPlaylist: (String) -> Unit,
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
                otherArtists = s.otherArtists,
                navigateToAlbum = navigateToAlbum,
                navigateToArtist = navigateToArtist,
                navigateToPlaylist = navigateToPlaylist,
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
    otherArtists: List<ArtistUiModel>,
    navigateToAlbum: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    navigateToPlaylist: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    ScrollableTopBarSurface(
        modifier = modifier,
        imageUrl = artist.imageUrl,
        dominantColor = artist.dominantColor,
        title = artist.name,
        onBack = navigateBack,
        header = {
            ArtistInfoTitle(
                modifier = it,
                artist = artist,
            )
        }
    ) {
        SimpleTopTrackListInfo(
            tracks = topTracks,
            onClick = { /* todo */ }
        )

        playlists.firstOrNull()?.let { playlist ->
            ListTitle(title = "아티스트 추천") {
                RepresentativePlaylist(
                    artistImageUrl = artist.imageUrl,
                    albumImageUrl = playlist.imageUrl,
                    description = playlist.description,
                    title = playlist.name,
                    subTitle = "플레이리스트",
                    onClick = {
                        navigateToPlaylist(playlist.id)
                    }
                )
            }
        }

        ListTitle(
            title = "인기음악",
            onMore = { /* todo */ },
        ) {
            SimpleAlbumListInfo(
                albums = albums,
                onMore = { /* todo */ },
                onAlbum = { albumId ->
                     navigateToAlbum(albumId)
                }
            )
        }

        ListTitle(title = "상세정보") {
            ArtistDetailInfo(
                artist = artist,
                onClick = { /* todo */ }
            )
        }

        ListTitle(title = "아티스트 플레이리스트") {
            PlaylistInfo(
                playlists = playlists.drop(1),
                onPlaylist = navigateToPlaylist
            )
        }

        ListTitle(title = "팬들이 좋아하는 다른 음악") {
            OtherArtistInfo(
                artists = otherArtists,
                onClick = navigateToArtist
            )
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
fun SimpleTopTrackListInfo(
    modifier: Modifier = Modifier,
    tracks: List<TrackUiModel>,
    onClick: () -> Unit
) {
    val text = buildAnnotatedString {
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

    Text(
        modifier = modifier
            .clickableScaled { onClick() },
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 32.sp),
    )
}

@Composable
fun RepresentativePlaylist(
    modifier: Modifier = Modifier,
    artistImageUrl: String?,
    albumImageUrl: String?,
    description: String,
    title: String,
    subTitle: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickableScaled { onClick() },
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            model = artistImageUrl,
            contentDescription = "Artist Thumbnail",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            placeholder = rememberVectorPainter(image = Icons.Default.Album),
            error = rememberVectorPainter(image = Icons.Default.Error),
            colorFilter = ColorFilter.tint(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                blendMode = BlendMode.Multiply,
            )
        )

        if (description.isNotEmpty()) {
            ChipDescription(
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        start = 16.dp
                    ),
                imageUrl = artistImageUrl,
                description = description,
            )
        }

        RepresentativeAlbumInfo(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    bottom = 16.dp,
                    start = 16.dp
                ),
            imageUrl = albumImageUrl,
            title = title,
            subTitle = subTitle,
        )
    }
}

@Composable
fun SimpleAlbumListInfo(
    albums: List<AlbumUiModel>,
    onMore: () -> Unit,
    onAlbum: (String) -> Unit
) {
    albums.take(4).forEachIndexed { index, album ->
        ListItemVerticalMedium(
            imageUrl = album.imageUrl,
            title = album.name,
            subtitle = album.artists,
            onClick = { onAlbum(album.id) }
        )
    }

    BorderButton(
        modifier = Modifier
            .width(180.dp),
        textInactive = "디스코그래피 보기",
        onClick = onMore
    )
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
                                val format = NumberFormat.getNumberInstance(Locale.US)
                                    .format(artist.follower)
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

@Composable
fun PlaylistInfo(
    modifier: Modifier = Modifier,
    playlists: List<PlaylistUiModel>,
    onPlaylist: (String) -> Unit
) {
    LazyRow(
        modifier = modifier,
        state = rememberLazyListState(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(playlists.size) { index ->
            val playlist = playlists[index]
            ListItemHorizontalMedium(
                imageUrl = playlist.imageUrl,
                title = playlist.name,
                onClick = { onPlaylist(playlist.id) }
            )
        }
    }
}

@Composable
fun OtherArtistInfo(
    modifier: Modifier = Modifier,
    artists: List<ArtistUiModel>,
    onClick: (String) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        state = rememberLazyListState(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(artists.size) { index ->
            val artist = artists[index]
            ListItemHorizontalMedium(
                imageUrl = artist.imageUrl,
                shape = CircleShape,
                title = artist.name,
                onClick = { onClick(artist.id) }
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
            otherArtists = PreviewArtistUiModels,
            navigateToAlbum = { },
            navigateToArtist = { },
            navigateToPlaylist = { },
            navigateBack = { }
        )
    }
}

@DevicePreviews
@Composable
private fun ArtistInfoTitlePreview() {
    SpotifyTheme {
        ArtistInfoTitle(artist = PreviewArtistUiModel)
    }
}

@DevicePreviews
@Composable
private fun SimpleTopTrackListInfoPreview() {
    SpotifyTheme {
        SimpleTopTrackListInfo(
            tracks = PreviewTrackUiModels,
            onClick = { }
        )
    }
}

@DevicePreviews
@Composable
private fun RepresentativePlaylistPreview() {
    SpotifyTheme {
        RepresentativePlaylist(
            artistImageUrl = null,
            albumImageUrl = null,
            description = "‘IM THE DRAMA' OUT NOW \uD83D\uDC85 Tap the ⨁ to be the first to hear new Bebe Rexha songs as soon as they’re released.",
            title = "Bebe Rexha - I'm The Drama",
            subTitle = "플레이리스트",
            onClick = { }
        )
    }
}

@DevicePreviews
@Composable
private fun ArtistDetailInfoPreview() {
    SpotifyTheme {
        ArtistDetailInfo(
            artist = PreviewArtistUiModel,
            onClick = { }
        )
    }
}

@DevicePreviews
@Composable
private fun PlaylistInfoPreview() {
    SpotifyTheme {
        PlaylistInfo(
            playlists = PreviewPlaylistUiModels,
            onPlaylist = { }
        )
    }
}

@DevicePreviews
@Composable
private fun OtherArtistInfoPreview() {
    SpotifyTheme {
        OtherArtistInfo(
            artists = PreviewArtistUiModels,
            onClick = { }
        )
    }
}