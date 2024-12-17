package com.litbig.spotify.core.domain.usecase.favorite

import com.litbig.spotify.core.domain.model.local.Favorite
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend fun toggleFavoriteTrack(trackName: String, imageUrl: String?): Boolean {
        if (repository.isFavorite(trackName, "track").first()) {
            repository.deleteFavorite(trackName, "track")
            return false
        } else {
            repository.insertFavorite(Favorite(trackName, "track", imageUrl))
            return true
        }
    }

    suspend fun toggleFavoriteAlbum(albumName: String, imageUrl: String?): Boolean {
        if (repository.isFavorite(albumName, "album").first()) {
            repository.deleteFavorite(albumName, "album")
            return false
        } else {
            repository.insertFavorite(Favorite(albumName, "album", imageUrl))
            return true
        }
    }

    suspend fun toggleFavoriteArtist(artistName: String, imageUrl: String?): Boolean {
        if (repository.isFavorite(artistName, "artist").first()) {
            repository.deleteFavorite(artistName, "artist")
            return false
        } else {
            repository.insertFavorite(Favorite(artistName, "artist", imageUrl))
            return true
        }
    }
}