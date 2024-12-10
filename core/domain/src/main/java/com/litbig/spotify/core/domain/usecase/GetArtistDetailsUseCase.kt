package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.repository.MusicRepository
import timber.log.Timber
import javax.inject.Inject

class GetArtistDetailsUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(artistId: String): ArtistDetails {
        Timber.i("artistId: $artistId")
        return musicRepository.getArtistDetails(artistId)
    }
}