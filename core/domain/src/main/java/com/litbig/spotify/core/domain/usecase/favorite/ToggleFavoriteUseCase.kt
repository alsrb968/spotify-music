package com.litbig.spotify.core.domain.usecase.favorite

import com.litbig.spotify.core.domain.model.local.Favorite
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend fun toggleFavoriteTrack(trackName: String, imageUrl: String?) {
        if (repository.isFavorite(trackName, "track").first()) {
            repository.deleteFavorite(trackName, "track")
        } else {
            repository.insertFavorite(Favorite(trackName, "track", imageUrl))
        }
    }

    suspend fun toggleFavoriteAlbum(albumName: String, imageUrl: String?) {
        if (repository.isFavorite(albumName, "album").first()) {
            repository.deleteFavorite(albumName, "album")
        } else {
            repository.insertFavorite(Favorite(albumName, "album", imageUrl))
        }
    }

    suspend fun toggleFavoriteArtist(artistName: String, imageUrl: String?) {
        if (repository.isFavorite(artistName, "artist").first()) {
            repository.deleteFavorite(artistName, "artist")
        } else {
            repository.insertFavorite(Favorite(artistName, "artist", imageUrl))
        }
    }
}