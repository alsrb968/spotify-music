package com.litbig.spotify.ui.models

import com.litbig.spotify.core.domain.model.remote.ArtistDetails

data class ArtistUiModel(
    val id: String,
    val imageUrl: String?,
    val name: String,
    val follower: Int,
    val popularity: Int,
    val genres: String,
) {
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
            )
        }
    }
}