package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.remote.Albums
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetNewAlbumReleasesUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(limit: Int = 10, offset: Int = 0): Flow<Albums> {
        return flow {
            val albums = repository.getNewAlbumReleases(limit, offset)
            albums?.let {
                emit(it)
            }
        }
    }
}