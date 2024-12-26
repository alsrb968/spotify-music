@file:OptIn(ExperimentalMaterial3Api::class)

package com.litbig.spotify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.litbig.spotify.ui.models.ArtistUiModel
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewAlbumArt
import com.litbig.spotify.ui.tooling.PreviewArtistUiModels
import com.litbig.spotify.ui.tooling.PreviewTrackUiModel
import kotlinx.coroutines.launch

@Composable
fun ArtistsBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    artists: List<ArtistUiModel>,
    onArtistSelected: (String) -> Unit,
    onShow: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = { onShow(false) },
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = true,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier,
                text = "아티스트",
                style = MaterialTheme.typography.headlineSmall,
            )

            artists.forEach { artist ->
                ListItemVerticalMedium(
                    imageUrl = artist.imageUrl,
                    imageSize = 60.dp,
                    shape = CircleShape,
                    title = artist.name,
                    placeholder = PreviewAlbumArt(),
                    onClick = {
                        scope.launch { sheetState.hide() }
                        onArtistSelected(artist.id)
                    }
                )
            }
        }
    }
}

@Composable
fun MenuBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onShow: (Boolean) -> Unit,
    header: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = { onShow(false) },
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = true,
        ),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                header()
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp,
            )

            Column(
//                modifier = Modifier
//                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                content()
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ArtistsBottomSheetPreview() {
    SpotifyTheme {
        ArtistsBottomSheet(
            sheetState = rememberStandardBottomSheetState(),
            artists = PreviewArtistUiModels,
            onArtistSelected = {},
            onShow = {},
        )
    }
}

@DevicePreviews
@Composable
private fun MenuBottomSheetPreview() {
    SpotifyTheme {
        MenuBottomSheet(
            sheetState = rememberStandardBottomSheetState(),
            onShow = {},
            header = {
                ListItemVerticalMedium(
                    imageUrl = null,
                    imageSize = 60.dp,
                    title = PreviewTrackUiModel.name,
                    subtitle = PreviewTrackUiModel.artists,
                    placeholder = PreviewAlbumArt(),
                    onClick = {},
                )
            },
            content = {
                MenuIconItem(
                    imageVector = Icons.Default.Favorite,
                    title = "좋아요 표시한 곡에 추가",
                )
                MenuIconItem(
                    imageVector = Icons.Default.Favorite,
                    title = "플레이리스트에 추가",
                )
                MenuIconItem(
                    imageVector = Icons.Default.Favorite,
                    title = "아티스트로 이동하기",
                )
                MenuIconItem(
                    imageVector = Icons.Default.Favorite,
                    title = "공유",
                )
                MenuIconItem(
                    imageVector = Icons.Default.Favorite,
                    title = "좋아요 표시한 곡에 추가",
                )
                MenuIconItem(
                    imageVector = Icons.Default.Favorite,
                    title = "플레이리스트에 추가",
                )
                MenuIconItem(
                    imageVector = Icons.Default.Favorite,
                    title = "아티스트로 이동하기",
                )
                MenuIconItem(
                    imageVector = Icons.Default.Favorite,
                    title = "공유",
                )
            }
        )
    }
}