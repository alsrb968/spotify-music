package com.litbig.spotify.core.domain.usecase

import androidx.paging.PagingData
import com.litbig.spotify.core.domain.model.Artist
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
//    operator fun invoke(pageSize: Int = 20): Flow<PagingData<Artist>> {
//
//    }
}