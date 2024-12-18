package com.litbig.spotify.core.domain.usecase.spotify

import com.litbig.spotify.core.domain.model.remote.TrackDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import javax.inject.Inject

class GetSeveralTrackDetailsUseCase @Inject constructor(
    private val repository: SpotifyRepository
) {
    suspend operator fun invoke(trackIds: String): List<TrackDetails> {
        return repository.getSeveralTrackDetails(trackIds)
    }
}