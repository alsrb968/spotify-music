package com.litbig.spotify.core.domain.usecase.spotify

import com.litbig.spotify.core.domain.model.remote.Albums
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import javax.inject.Inject

class GetNewAlbumReleasesUseCase @Inject constructor(
    private val repository: SpotifyRepository
) {
    suspend operator fun invoke(limit: Int = 10, offset: Int = 0): Albums? {
        return repository.getNewAlbumReleases(limit, offset)
    }
}