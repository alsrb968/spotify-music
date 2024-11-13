package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.repository.MusicRepository
import javax.inject.Inject

class GetMetadataUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(pageSize: Int = 20) = musicRepository.getMetadata(pageSize)
}