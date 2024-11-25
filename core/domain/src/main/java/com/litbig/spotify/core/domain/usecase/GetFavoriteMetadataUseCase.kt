package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.repository.MusicRepository
import javax.inject.Inject

class GetFavoriteMetadataUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(pageSize: Int = 20) = repository.getFavoriteMetadata(pageSize)
}