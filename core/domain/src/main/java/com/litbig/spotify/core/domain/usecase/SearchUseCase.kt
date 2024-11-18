package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.remote.Search
import com.litbig.spotify.core.domain.repository.MusicRepository
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(query: String, type: String): Search {
        return repository.search(query, type)
    }
}