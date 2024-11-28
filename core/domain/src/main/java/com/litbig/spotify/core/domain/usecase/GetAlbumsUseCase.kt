package com.litbig.spotify.core.domain.usecase

import androidx.paging.PagingData
import com.litbig.spotify.core.domain.extension.mapAsync
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(pageSize: Int = 20): Flow<PagingData<MusicInfo>> {
        return repository.getPagedAlbums(pageSize).flatMapLatest { pagingData ->
            pagingData.mapAsync { albumName ->
                val artist = repository.getArtistFromAlbum(albumName)
                val imageUrl = repository.getAlbumArtByAlbum(albumName)?.imageUrl
                val musicCount = repository.getMetadataCountByAlbum(albumName)

                if (imageUrl == null) {
                    Timber.w("$albumName has no image")
                }

                MusicInfo(
                    imageUrl = imageUrl,
                    title = albumName,
                    content = "$artist • $musicCount songs",
                    category = "album"
                )
            }
        }
    }

    fun getAlbums(count: Int = 10): Flow<List<MusicInfo>> {
        return repository.getAlbums(count).map { albumNames ->
            albumNames.map { albumName ->
                val artist = repository.getArtistFromAlbum(albumName)
                val imageUrl = repository.getAlbumArtByAlbum(albumName)?.imageUrl
                val musicCount = repository.getMetadataCountByAlbum(albumName)

                if (imageUrl == null) {
                    Timber.w("$albumName has no image")
                }

                MusicInfo(
                    imageUrl = imageUrl,
                    title = albumName,
                    content = "$artist • $musicCount songs",
                    category = "album"
                )
            }
        }
    }
}