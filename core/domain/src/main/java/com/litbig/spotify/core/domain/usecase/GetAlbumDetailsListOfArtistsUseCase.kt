package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.remote.AlbumDetails
import com.litbig.spotify.core.domain.repository.MusicRepository
import javax.inject.Inject

class GetAlbumDetailsListOfArtistsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(vararg artistNames: String): List<List<AlbumDetails>> {
        return artistNames
            .mapNotNull { repository.searchArtist(it) }
            .map { repository.getAlbumsOfArtist(it.id).items }
    }
}