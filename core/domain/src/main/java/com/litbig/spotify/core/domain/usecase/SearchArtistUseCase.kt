package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.repository.MusicRepository
import javax.inject.Inject

class SearchArtistUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(vararg artistNames: String): List<ArtistDetails> {
        return artistNames.mapNotNull { artistName ->
            repository.searchArtist(artistName)
        }
    }
}