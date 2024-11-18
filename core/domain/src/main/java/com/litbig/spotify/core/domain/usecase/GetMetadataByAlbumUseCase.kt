package com.litbig.spotify.core.domain.usecase

import androidx.paging.PagingData
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMetadataByAlbumUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(albumName: String, pageSize: Int = 20): Flow<PagingData<MusicMetadata>> {
        return repository.getMetadataByAlbum(albumName, pageSize)
    }
}