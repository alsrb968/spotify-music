package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.remote.AlbumDetails
import com.litbig.spotify.core.domain.repository.MusicRepository
import javax.inject.Inject

class GetAlbumDetailsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(albumId: String): AlbumDetails {
        return repository.getAlbumDetails(albumId)
    }
}