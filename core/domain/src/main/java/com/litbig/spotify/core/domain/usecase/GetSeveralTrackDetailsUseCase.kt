package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.remote.TrackDetails
import com.litbig.spotify.core.domain.repository.MusicRepository
import javax.inject.Inject

class GetSeveralTrackDetailsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(trackIds: String): List<TrackDetails> {
        return repository.getSeveralTrackDetails(trackIds)
    }
}