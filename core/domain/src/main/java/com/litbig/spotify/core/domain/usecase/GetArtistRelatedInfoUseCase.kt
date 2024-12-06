package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.remote.AlbumDetails
import com.litbig.spotify.core.domain.model.remote.TrackDetails
import com.litbig.spotify.core.domain.repository.MusicRepository
import javax.inject.Inject

class GetArtistRelatedInfoUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(artistName: String): Pair<List<AlbumDetails>, List<TrackDetails>>? {
        return repository.searchArtist(artistName)?.let { artistDetails ->
            val artistId = artistDetails.id
            val albums = repository.getAlbumsOfArtist(artistId).items
            val tracks = repository.getTopTracksOfArtist(artistId)
            Pair(albums, tracks)
        }
    }
}