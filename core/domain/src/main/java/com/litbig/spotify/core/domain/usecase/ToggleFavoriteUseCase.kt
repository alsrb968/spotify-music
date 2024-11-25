package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(absolutePath: String) = withContext(Dispatchers.IO) {
        val isFavorite = repository.getFavorite(absolutePath)
        repository.updateFavorite(absolutePath, !isFavorite)
    }
}