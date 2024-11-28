package com.litbig.spotify.core.domain.usecase.metadata

import androidx.paging.PagingData
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMetadataUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(pageSize: Int = 20): Flow<PagingData<MusicMetadata>> {
        return musicRepository.getMetadata(pageSize)
    }
}