package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.remote.AlbumDetails
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAlbumDetailsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(albumId: String): Flow<AlbumDetails> {
        return flow {
            emit(repository.getAlbumDetails(albumId))
        }
    }
}