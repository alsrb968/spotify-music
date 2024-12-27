package com.litbig.spotify.ui.models

import androidx.compose.ui.graphics.Color
import com.litbig.spotify.core.domain.model.remote.ArtistDetails

data class ArtistUiModel(
    override val id: String,
    override val imageUrl: String?,
    override val name: String,
    val follower: Int,
    val popularity: Int,
    val genres: String,
    val dominantColor: Color,
) : UiModel {
    companion object {
        @JvmStatic
        fun from(artistDetails: ArtistDetails): ArtistUiModel {
            return ArtistUiModel(
                id = artistDetails.id,
                imageUrl = artistDetails.images?.firstOrNull()?.url,
                name = artistDetails.name,
                follower = artistDetails.followers?.total ?: 0,
                popularity = artistDetails.popularity ?: 0,
                genres = artistDetails.genres?.joinToString(", ") ?: "",
                dominantColor = Color.Transparent,
            )
        }
    }
}