package com.litbig.spotify.core.domain.usecase.spotify

import com.litbig.spotify.core.domain.model.remote.AlbumDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import javax.inject.Inject

class GetAlbumDetailsListOfArtistsUseCase @Inject constructor(
    private val repository: SpotifyRepository
) {
    suspend operator fun invoke(vararg artistNames: String): List<List<AlbumDetails>> {
        return artistNames
            .mapNotNull { repository.searchArtist(it) }
            .map { repository.getAlbumsOfArtist(it.id).items }
    }
}