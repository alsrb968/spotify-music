package com.litbig.spotify.ui.home.artist

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.model.remote.PlaylistDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import com.litbig.spotify.core.domain.usecase.spotify.GetAlbumDetailsListOfArtistsUseCase
import com.litbig.spotify.core.domain.usecase.spotify.GetArtistDetailsUseCase
import com.litbig.spotify.core.domain.usecase.spotify.GetTopTrackDetailsListOfArtistsUseCase
import com.litbig.spotify.ui.home.HomeSection
import com.litbig.spotify.ui.models.AlbumUiModel
import com.litbig.spotify.ui.models.ArtistUiModel
import com.litbig.spotify.ui.models.PlaylistUiModel
import com.litbig.spotify.ui.models.TrackUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ArtistDetailUiState {
    data object Loading : ArtistDetailUiState
    data class Ready(
        val artist: ArtistUiModel,
        val albums: List<AlbumUiModel>,
        val topTracks: List<TrackUiModel>,
        val playlists: List<PlaylistUiModel>,
    ) : ArtistDetailUiState
}

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getArtistDetailsUseCase: GetArtistDetailsUseCase,
//    private val getAlbumDetailsListOfArtistsUseCase: GetAlbumDetailsListOfArtistsUseCase,
//    private val getTopTrackDetailsListOfArtistsUseCase: GetTopTrackDetailsListOfArtistsUseCase,
    private val spotifyRepository: SpotifyRepository,
) : ViewModel() {
    private val artistId = Uri.decode(savedStateHandle.get<String>(HomeSection.ARG_ARTIST_ID))

    private val dominantColor = MutableStateFlow(Color.Transparent)

    private val artistDetails = flow { emit(spotifyRepository.getArtistDetails(artistId)) }
    private val albums = flow { emit(spotifyRepository.getAlbumsOfArtist(artistId)) }
    private val topTracks = flow { emit(spotifyRepository.getTopTracksOfArtist(artistId)) }

    private val playlists = MutableStateFlow<List<PlaylistDetails>>(emptyList())

    val state: StateFlow<ArtistDetailUiState> = combine(
        artistDetails,
        albums,
        topTracks,
        playlists,
        ) { artist, albums, tracks, playlists ->
        ArtistDetailUiState.Ready(
            artist = ArtistUiModel.from(artist),
            albums = albums.items.map { AlbumUiModel.from(it) },
            topTracks = tracks.map { TrackUiModel.from(it) },
            playlists = playlists.map { PlaylistUiModel.from(it) },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ArtistDetailUiState.Loading
    )

    init {
        viewModelScope.launch {
            artistDetails.collectLatest { artistDetails ->
                artistDetails.name.let {
                    playlists.value = spotifyRepository.searchPlaylistOfArtist(it) ?: emptyList()
                }
            }
        }
    }

    private suspend fun getArtistDetails(artistId: String) =
        flowOf(getArtistDetailsUseCase(artistId))

    private suspend fun getAlbums(artistId: String) =
        flowOf(spotifyRepository.getAlbumsOfArtist(artistId))

    private suspend fun getTopTracks(artistId: String) =
        flowOf(spotifyRepository.getTopTracksOfArtist(artistId))

    private suspend fun getPlaylists(artistId: String) =
        flowOf(spotifyRepository.searchPlaylistOfArtist(artistId))
}