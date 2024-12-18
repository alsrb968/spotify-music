package com.litbig.spotify.core.domain.usecase.spotify

import com.litbig.spotify.core.domain.model.remote.Search
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repository: SpotifyRepository
) {
    suspend operator fun invoke(query: String, type: String): Search {
        return repository.search(query, type)
    }
}