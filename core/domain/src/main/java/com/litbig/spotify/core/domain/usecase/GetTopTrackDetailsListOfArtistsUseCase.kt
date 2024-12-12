package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.remote.TrackDetails
import com.litbig.spotify.core.domain.repository.MusicRepository
import javax.inject.Inject

class GetTopTrackDetailsListOfArtistsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(vararg artistNames: String): List<List<TrackDetails>> {
        return artistNames
            .mapNotNull { repository.searchArtist(it) }
            .map { repository.getTopTracksOfArtist(it.id) }
    }
}