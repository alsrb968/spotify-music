@file:OptIn(ExperimentalMaterial3Api::class)

package com.litbig.spotify.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.R
import com.litbig.spotify.core.design.extension.clickableScaled
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.ui.components.ListItemVerticalMedium
import com.litbig.spotify.ui.components.ScalableIconButton
import com.litbig.spotify.ui.components.SpotifyFilterChips
import com.litbig.spotify.ui.models.*
import com.litbig.spotify.ui.shared.Loading
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.*

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
    navigateToAlbum: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    navigateToPlaylist: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is LibraryUiState.Loading -> {
            Loading(modifier = modifier.fillMaxSize())
        }

        is LibraryUiState.Ready -> {
            val tracks = s.uiModels.filterIsInstance<TrackUiModel>()
            val remains = s.uiModels.filter { it !is TrackUiModel }

            LibraryScreen(
                modifier = modifier,
                tracks = tracks,
                remains = remains,
                navigateToAlbum = navigateToAlbum,
                navigateToArtist = navigateToArtist,
                navigateToPlaylist = navigateToPlaylist,
            )
        }
    }
}

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    tracks: List<TrackUiModel>,
    remains: List<UiModel>,
    navigateToAlbum: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    navigateToPlaylist: (String) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LibraryTopBar()
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            ContentTitle()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {

                item {
                    ListItemVerticalMedium(
                        imageUrl = null,
                        placeholder = painterResource(id = R.drawable.liked_medium),
                        error = painterResource(id = R.drawable.liked_medium),
                        imageSize = 68.dp,
                        shape = RectangleShape,
                        title = stringResource(id = R.string.liked_songs),
                        subtitleContent = {
                            val (text, inlineContent) = buildIconWithText(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_pinned),
                                tint = MaterialTheme.colorScheme.primary,
                                text = "%s %s %s".format(
                                    stringResource(id = R.string.playlist),
                                    stringResource(id = R.string.bullet),
                                    "곡 ${tracks.size}개",
                                )
                            )
                            Text(
                                text = text,
                                inlineContent = inlineContent,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        onClick = { /* todo */ },
                    )
                }

                items(remains.size) { index ->
                    val size = 68.dp
                    when (val remain = remains[index]) {
                        is AlbumUiModel -> {
                            val subtitle = "%s %s %s".format(
                                stringResource(id = R.string.album),
                                stringResource(id = R.string.bullet),
                                remain.artists,
                            )
                            ListItemVerticalMedium(
                                imageUrl = remain.imageUrl,
                                title = remain.name,
                                subtitle = subtitle,
                                imageSize = size,
                                shape = RectangleShape,
                                onClick = { navigateToAlbum(remain.id) },
                            )
                        }

                        is ArtistUiModel -> {
                            ListItemVerticalMedium(
                                imageUrl = remain.imageUrl,
                                title = remain.name,
                                subtitle = stringResource(id = R.string.artist),
                                imageSize = size,
                                shape = CircleShape,
                                onClick = { navigateToArtist(remain.id) },
                            )
                        }

                        is PlaylistUiModel -> {
                            ListItemVerticalMedium(
                                imageUrl = remain.imageUrl,
                                title = remain.name,
                                subtitle = stringResource(id = R.string.playlist),
                                imageSize = size,
                                shape = RectangleShape,
                                onClick = { navigateToPlaylist(remain.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryTopBar(
    modifier: Modifier = Modifier,
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(statusBarHeight + 16.dp))

        LibraryTitle(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            title = stringResource(id = R.string.home_library),
            onSearch = { /* TODO */ },
            onAdd = { /* TODO */ },
        )

        SpotifyFilterChips(
            modifier = Modifier
                .fillMaxWidth(),
            cancelable = true,
            filters = listOf(
                stringResource(id = R.string.playlist),
                stringResource(id = R.string.podcast),
                stringResource(id = R.string.album),
                stringResource(id = R.string.artist),
            ),
            onFilterSelected = { filter ->

            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .gradientBackground(
                    ratio = 0.5f,
                    startColor = Color.Black,
                    endColor = MaterialTheme.colorScheme.background,
                )
        )
    }
}

@Composable
fun LibraryTitle(
    modifier: Modifier = Modifier,
    title: String,
    onSearch: () -> Unit,
    onAdd: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        ScalableIconButton(
            modifier = Modifier,
            onClick = onSearch,
        ) {
            Icon(
                modifier = Modifier
                    .size(20.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_search_normal),
                contentDescription = "Search",
            )
        }

        ScalableIconButton(
            modifier = Modifier,
            onClick = onAdd,
        ) {
            Icon(
                modifier = Modifier
                    .size(20.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_add),
                contentDescription = "Add",
            )
        }
    }
}

@Composable
fun ContentTitle(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .clickableScaled { /* todo */ },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_sort),
                contentDescription = "Sort",
            )
            Text(
                text = stringResource(id = R.string.recent),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        ScalableIconButton(
            onClick = { /* todo */ },
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_grid),
                contentDescription = "Grid",
            )
        }
    }
}

@Composable
fun buildIconWithText(
    imageVector: ImageVector,
    tint: Color,
    text: String,
): Pair<AnnotatedString, Map<String, InlineTextContent>> {

    val inlineContent = mapOf(
        "icon" to InlineTextContent(
            placeholder = Placeholder(
                width = 12.sp,
                height = 12.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            ),
            children = {
                Icon(
                    imageVector = imageVector,
                    contentDescription = "icon",
                    tint = tint,
                )
            }
        )
    )

    val annotatedString = buildAnnotatedString {
        appendInlineContent("icon", "[icon]")
        append(" ")
        append(text)
    }

    return Pair(annotatedString, inlineContent)
}

@DevicePreviews
@Composable
private fun LibraryScreenPreview() {
    SpotifyTheme {
        LibraryScreen(
            tracks = PreviewTrackUiModels,
            remains = PreviewAlbumUiModels.take(3) +
                    PreviewArtistUiModels.take(3) +
                    PreviewPlaylistUiModels.take(3),
            navigateToAlbum = { },
            navigateToArtist = { },
            navigateToPlaylist = { },
        )
    }
}