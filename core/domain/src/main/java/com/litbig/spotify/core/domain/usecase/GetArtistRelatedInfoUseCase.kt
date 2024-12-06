package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.remote.AlbumDetails
import com.litbig.spotify.core.domain.model.remote.TrackDetails
import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class GetArtistRelatedInfoUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(artistName: String): Flow<Pair<List<AlbumDetails>, List<TrackDetails>>> {
        return flow {
            repository.searchArtist(artistName)?.let { artistDetails ->
                val artistId = artistDetails.id
                Timber.i("artistId: $artistId")
                val albums = repository.getAlbumsOfArtist(artistId).items
                Timber.i("albums.size: ${albums.size}")
                val tracks = repository.getTopTracksOfArtist(artistId)
                Timber.i("tracks.size: ${tracks.size}")
                emit(Pair(albums, tracks))
            }
        }
    }
}