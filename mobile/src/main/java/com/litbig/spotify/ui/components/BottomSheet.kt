@file:OptIn(ExperimentalMaterial3Api::class)

package com.litbig.spotify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.litbig.spotify.ui.models.ArtistUiModel
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewArtistUiModels
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
                    onClick = {
                        scope.launch { sheetState.hide() }
                        onArtistSelected(artist.id)
                    }
                )
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