package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.remote.TrackDetails
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTrackDetailsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(trackId: String): Flow<TrackDetails> {
        return flow {
            emit(repository.getTrackDetails(trackId))
        }
    }
}