package com.litbig.spotify.core.domain.usecase.favorite

import androidx.paging.PagingData
import com.litbig.spotify.core.domain.extension.mapAsync
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(pageSize: Int = 20): Flow<PagingData<MusicInfo>> {
        return repository.getPagedFavorites(pageSize).flatMapLatest { pagingData ->
            pagingData.mapAsync { favorite ->
                MusicInfo(
                    imageUrl = favorite.imageUrl,
                    title = favorite.name,
                    content = when (favorite.type) {
                        "track" -> "노래"
                        "album" -> "앨범"
                        "artist" -> "아티스트"
                        else -> ""
                    },
                    category = "favorite"
                )
            }
        }
    }
}