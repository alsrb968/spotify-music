package com.litbig.spotify.core.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.litbig.spotify.core.domain.model.Album
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(pageSize: Int = 20): Flow<PagingData<Album>> {
        return repository.getPagedAlbums(pageSize).map { pagingData ->
            pagingData.map { albumName ->
                val firstMetadata = repository.getMetadataByAlbum(albumName).first()
                val musicCount = repository.getMetadataCountByAlbum(albumName)

                val (artist, albumArt) = firstMetadata.artistName to firstMetadata.albumArt

                Album(
                    name = albumName,
                    artist = artist,
                    albumArt = albumArt,
                    musicCount = musicCount
                )
            }
        }
    }
}