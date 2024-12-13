package com.litbig.spotify.ui.models

import com.litbig.spotify.core.domain.model.remote.AlbumDetails
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.model.remote.TrackDetails

data class FeedUiModel(
    val id: String,
    val imageUrl: String?,
    val name: String,
    val type: String,
) {
    companion object {
        @JvmStatic
        fun from(album: AlbumDetails): FeedUiModel {
            return FeedUiModel(
                id = album.id,
                imageUrl = album.images.firstOrNull()?.url,
                name = album.name,
                type = "album",
            )
        }

        @JvmStatic
        fun from(artist: ArtistDetails): FeedUiModel {
            return FeedUiModel(
                id = artist.id,
                imageUrl = artist.images?.firstOrNull()?.url,
                name = artist.name,
                type = "artist",
            )
        }

        @JvmStatic
        fun from(track: TrackDetails): FeedUiModel {
            return FeedUiModel(
                id = track.id,
                imageUrl = track.album?.images?.firstOrNull()?.url,
                name = track.name,
                type = "track",
            )
        }
    }
}