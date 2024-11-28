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

class GetArtistsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(pageSize: Int = 20): Flow<PagingData<MusicInfo>> {
        return repository.getPagedArtists(pageSize).flatMapLatest { pagingData ->
            pagingData.mapAsync { artistName ->
                val imageUrl = repository.getArtistInfoByArtist(artistName)?.imageUrl
                val albumCount = repository.getMetadataCountByAlbumOfArtist(artistName)
                val musicCount = repository.getMetadataCountByArtist(artistName)

                if (imageUrl == null) {
                    Timber.w("$artistName has no image")
                }

                MusicInfo(
                    imageUrl = imageUrl,
                    title = artistName,
                    content = "$albumCount albums • $musicCount songs",
                    category = "artist"
                )
            }
        }
    }

    fun getArtists(count: Int = 10): Flow<List<MusicInfo>> {
        return repository.getArtists(count).map { artistNames ->
            artistNames.map { artistName ->
                val imageUrl = repository.getArtistInfoByArtist(artistName)?.imageUrl
                val albumCount = repository.getMetadataCountByAlbumOfArtist(artistName)
                val musicCount = repository.getMetadataCountByArtist(artistName)

                if (imageUrl == null) {
                    Timber.w("$artistName has no image")
                }

                MusicInfo(
                    imageUrl = imageUrl,
                    title = artistName,
                    content = "$albumCount albums • $musicCount songs",
                    category = "artist"
                )
            }
        }
    }
}