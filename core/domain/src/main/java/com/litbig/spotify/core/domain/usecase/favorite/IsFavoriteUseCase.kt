package com.litbig.spotify.core.domain.usecase.favorite

import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    fun isFavoriteTrack(trackName: String): Flow<Boolean> {
        return repository.isFavorite(trackName, "track")
    }

    fun isFavoriteAlbum(albumName: String): Flow<Boolean> {
        return repository.isFavorite(albumName, "album")
    }

    fun isFavoriteArtist(artistName: String): Flow<Boolean> {
        return repository.isFavorite(artistName, "artist")
    }
}