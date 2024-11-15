package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.model.AlbumDetails
import com.litbig.spotify.core.domain.model.ArtistDetails
import com.litbig.spotify.core.domain.model.TrackDetails
import com.litbig.spotify.core.domain.repository.MusicRepository
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend fun searchTrack(trackName: String, artistName: String): Result<TrackDetails> {
        return repository.search(query = trackName, type = Type.TRACK).let { search ->
            search.tracks?.items?.firstOrNull { trackDetails ->
                trackDetails.artists.any { it.name == artistName }
            }?.let { track ->
                Result.success(track)
            } ?: Result.failure(Exception("Track not found"))
        }
    }

    suspend fun searchArtist(artistName: String): Result<ArtistDetails> {
        return repository.search(query = artistName, type = Type.ARTIST).let { search ->
            search.artists?.items?.firstOrNull()?.let { artistDetails ->
                Result.success(artistDetails)
            } ?: Result.failure(Exception("Artist not found"))
        }
    }

    suspend fun searchAlbum(albumName: String, artistName: String): Result<AlbumDetails> {
        return repository.search(query = albumName, type = Type.ALBUM).let { search ->
            search.albums?.items?.firstOrNull { albumDetails ->
                albumDetails.artists.any { it.name == artistName }
            }?.let { album ->
                Result.success(album)
            } ?: Result.failure(Exception("Album not found"))
        }
    }

    private object Type {
        const val TRACK = "track"
        const val ARTIST = "artist"
        const val ALBUM = "album"
    }
}