package com.litbig.spotify.core.domain.usecase.spotify

import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class GetSeveralArtistDetailsUseCase @Inject constructor(
    private val repository: SpotifyRepository
) {
    operator fun invoke(artistIds: String): Flow<List<ArtistDetails>> {
        Timber.v("artistIds: $artistIds")
        return flow { emit(repository.getSeveralArtistDetails(artistIds)) }
    }
}