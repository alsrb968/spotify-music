package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.remote.Albums
import com.litbig.spotify.core.domain.repository.MusicRepository
import javax.inject.Inject

class GetNewAlbumReleasesUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(limit: Int = 10, offset: Int = 0): Albums? {
        return repository.getNewAlbumReleases(limit, offset)
    }
}