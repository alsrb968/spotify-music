package com.litbig.spotify.core.domain.usecase.spotify

import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import javax.inject.Inject

class GetArtistDetailsUseCase @Inject constructor(
    private val repository: SpotifyRepository
) {
    suspend operator fun invoke(artistId: String): ArtistDetails {
        return repository.getArtistDetails(artistId)
    }
}