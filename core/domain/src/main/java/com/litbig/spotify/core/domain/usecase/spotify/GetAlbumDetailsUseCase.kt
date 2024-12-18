package com.litbig.spotify.core.domain.usecase.spotify

import com.litbig.spotify.core.domain.model.remote.AlbumDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAlbumDetailsUseCase @Inject constructor(
    private val repository: SpotifyRepository
) {
    operator fun invoke(albumId: String): Flow<AlbumDetails> {
        return flow {
            emit(repository.getAlbumDetails(albumId))
        }
    }
}