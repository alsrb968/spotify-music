package com.litbig.spotify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.litbig.spotify.core.design.extension.gradientBackground
import com.litbig.spotify.core.design.theme.Gotham
import com.litbig.spotify.core.design.theme.ProductSans
import com.litbig.spotify.core.design.theme.YoutubeSans
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import com.litbig.spotify.ui.tooling.PreviewAlbumArt
import com.litbig.spotify.ui.tooling.PreviewArtistUiModel
import com.litbig.spotify.ui.tooling.PreviewPlaylistUiModel

val COLLAPSED_TOP_BAR_HEIGHT = 90.dp
val EXPANDED_TOP_BAR_HEIGHT = 300.dp

@Composable
fun ExpandedTopBar(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    title: String,
    dominantColor: Color = MaterialTheme.colorScheme.background,
    scrollProgress: Float = 0f,
    placeholder: Painter? = rememberVectorPainter(image = Icons.Default.Album),
) {
    Box(
        modifier = modifier
            .background(dominantColor)
            .fillMaxWidth()
            .height(EXPANDED_TOP_BAR_HEIGHT),
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .alpha(scrollProgress)
                .scale(1.0f + scrollProgress * 0.1f),
            model = imageUrl,
            contentDescription = "Album Art",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            placeholder = placeholder,
            error = rememberVectorPainter(image = Icons.Default.Error)
        )

        Text(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .offset(y = -(EXPANDED_TOP_BAR_HEIGHT * (1f - scrollProgress) * 0.75f)),
            text = title,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ExpandedTopBarTracks(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    dominantColor: Color = MaterialTheme.colorScheme.background,
    scrollProgress: Float = 0f,
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .height(EXPANDED_TOP_BAR_HEIGHT)
            .offset(y = -(EXPANDED_TOP_BAR_HEIGHT * (1f - scrollProgress) * 0.75f))
    ) {

        Column(
            modifier = modifier
                .gradientBackground(
                    ratio = 0.8f,
                    startColor = dominantColor,
                    endColor = MaterialTheme.colorScheme.background,
                )
                .fillMaxWidth()
                .height(EXPANDED_TOP_BAR_HEIGHT)
                .padding(top = 48.dp)
//                .offset(y = -(EXPANDED_TOP_BAR_HEIGHT * (1f - scrollProgress) * 0.75f))
                ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun CollapsedTopBar(
    modifier: Modifier = Modifier,
    albumName: String,
    dominantColor: Color = MaterialTheme.colorScheme.background,
    progress: Float
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = modifier
            .background(dominantColor.copy(alpha = progress))
            .fillMaxWidth()
            .height(COLLAPSED_TOP_BAR_HEIGHT)

//            .padding(top = statusBarHeight, start = 16.dp, end = 16.dp)
        ,
        contentAlignment = Alignment.CenterStart
    ) {
        // 텍스트 애니메이션
        Text(
            modifier = Modifier
                .padding(start = 80.dp, top = statusBarHeight)
                .graphicsLayer {
                    alpha = progress.coerceIn(0f, 1f)
//                    translationY = (1f - progress) * 20f // 위로 이동
                },
            text = albumName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@DevicePreviews
@Composable
private fun ExpandedTopBarPreview() {
    SpotifyTheme {
        ExpandedTopBar(
            imageUrl = null,
            title = PreviewArtistUiModel.name,
            dominantColor = MaterialTheme.colorScheme.background,
            scrollProgress = 1f,
            placeholder = PreviewAlbumArt(),
        )
    }
}

@DevicePreviews
@Composable
private fun ExpandedTopBarTracksPreview() {
    SpotifyTheme {
        ExpandedTopBarTracks(
            title = PreviewPlaylistUiModel.name,
            subtitle = "여기에서 미리 듣기 곡을 이용할 수 있습니다. 전체 버전은 셔플 재생하세요.",
            dominantColor = MaterialTheme.colorScheme.primary,
            scrollProgress = 1f
        )
    }
}

@DevicePreviews
@Composable
private fun CollapsedTopBarPreview() {
    SpotifyTheme {
        CollapsedTopBar(
            albumName = PreviewArtistUiModel.name,
            dominantColor = MaterialTheme.colorScheme.background,
            progress = 1f
        )
    }
}