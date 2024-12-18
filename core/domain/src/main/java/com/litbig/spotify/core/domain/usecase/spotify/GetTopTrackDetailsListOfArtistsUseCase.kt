package com.litbig.spotify.core.domain.usecase.spotify

import com.litbig.spotify.core.domain.model.remote.TrackDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import javax.inject.Inject

class GetTopTrackDetailsListOfArtistsUseCase @Inject constructor(
    private val repository: SpotifyRepository
) {
    suspend operator fun invoke(vararg artistNames: String): List<List<TrackDetails>> {
        return artistNames
            .mapNotNull { repository.searchArtist(it) }
            .map { repository.getTopTracksOfArtist(it.id) }
    }
}