package com.litbig.spotify.core.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.litbig.spotify.core.domain.model.Artist
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetArtistsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(pageSize: Int = 20): Flow<PagingData<Artist>> {
        return repository.getPagedArtists(pageSize).map { pagingData ->
            pagingData.map { artistName ->
                val imageUrl = repository.getArtistInfoByArtist(artistName)?.imageUrl
                val albumCount = repository.getMetadataCountByAlbumOfArtist(artistName)
                val musicCount = repository.getMetadataCountByArtist(artistName)

                Artist(
                    name = artistName,
                    imageUrl = imageUrl,
                    albumCount = albumCount,
                    musicCount = musicCount
                )
            }
        }
    }
}