package com.litbig.spotify.ui.home.artist

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.design.extension.darkenColor
import com.litbig.spotify.core.domain.extension.combine
import com.litbig.spotify.core.domain.model.remote.PlaylistDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import com.litbig.spotify.core.domain.usecase.spotify.GetArtistDetailsUseCase
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
        val otherArtists: List<ArtistUiModel>,
    ) : ArtistDetailUiState
}

sealed interface ArtistDetailUiIntent {
    data class SetDominantColor(val color: Color) : ArtistDetailUiIntent
}

sealed interface ArtistDetailUiEffect {
    data class ShowToast(val message: String) : ArtistDetailUiEffect
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
    private val otherArtists = MutableStateFlow<List<ArtistUiModel>>(emptyList())

    val state: StateFlow<ArtistDetailUiState> = combine(
        artistDetails,
        albums,
        topTracks,
        playlists,
        otherArtists,
        dominantColor,
    ) { artist, albums, tracks, playlists, others, color ->
        ArtistDetailUiState.Ready(
            artist = ArtistUiModel.from(artist).copy(dominantColor = color),
            albums = albums.items.map { AlbumUiModel.from(it) },
            topTracks = tracks.map { TrackUiModel.from(it) },
            playlists = playlists.map { PlaylistUiModel.from(it) },
            otherArtists = others,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ArtistDetailUiState.Loading
    )

    private val _intent = MutableSharedFlow<ArtistDetailUiIntent>(extraBufferCapacity = 1)

    private val _effect = MutableSharedFlow<ArtistDetailUiEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<ArtistDetailUiEffect> = _effect

    init {
        viewModelScope.launch {
            artistDetails.collectLatest { artistDetails ->
                artistDetails.name.let { artistName ->
                    playlists.value = spotifyRepository.searchPlaylistOfArtist(artistName) ?: emptyList()
                    otherArtists.value = spotifyRepository.searchArtists(artistName)?.drop(1)?.map { ArtistUiModel.from(it) } ?: emptyList()
                }
            }
        }
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intent.collectLatest { intent ->
                when (intent) {
                    is ArtistDetailUiIntent.SetDominantColor -> setDominantColor(intent.color)
                }
            }
        }
    }

    fun sendIntent(intent: ArtistDetailUiIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private fun setDominantColor(color: Color) {
        dominantColor.value = color.darkenColor(0.5f)
    }
}