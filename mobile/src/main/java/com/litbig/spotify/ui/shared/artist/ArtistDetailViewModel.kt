package com.litbig.spotify.ui.shared.artist

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.design.extension.darkenColor
import com.litbig.spotify.core.domain.extension.combine
import com.litbig.spotify.core.domain.model.remote.AlbumDetails
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.model.remote.PlaylistDetails
import com.litbig.spotify.core.domain.model.remote.TrackDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import com.litbig.spotify.ui.models.AlbumUiModel
import com.litbig.spotify.ui.models.ArtistUiModel
import com.litbig.spotify.ui.models.PlaylistUiModel
import com.litbig.spotify.ui.models.TrackUiModel
import com.litbig.spotify.ui.shared.DetailsSection
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
        val isFavorite: Boolean,
    ) : ArtistDetailUiState
}

sealed interface ArtistDetailUiIntent {
    data object PlayTracks : ArtistDetailUiIntent
    data object ToggleFavorite : ArtistDetailUiIntent
    data object NavigateBack : ArtistDetailUiIntent
    data object NavigateToTrackDetail : ArtistDetailUiIntent
    data class NavigateToAlbumDetail(val albumId: String) : ArtistDetailUiIntent
    data class NavigateToArtistDetail(val artistId: String) : ArtistDetailUiIntent
    data class NavigateToPlaylistDetail(val playlistId: String) : ArtistDetailUiIntent
    data class SetDominantColor(val color: Color) : ArtistDetailUiIntent
}

sealed interface ArtistDetailUiEffect {
    data object NavigateBack : ArtistDetailUiEffect
    data object NavigateToTrackDetail : ArtistDetailUiEffect
    data class NavigateToAlbumDetail(val albumId: String) : ArtistDetailUiEffect
    data class NavigateToArtistDetail(val artistId: String) : ArtistDetailUiEffect
    data class NavigateToPlaylistDetail(val playlistId: String) : ArtistDetailUiEffect
    data class ShowToast(val message: String) : ArtistDetailUiEffect
}

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
//    private val getArtistDetailsUseCase: GetArtistDetailsUseCase,
//    private val getAlbumDetailsListOfArtistsUseCase: GetAlbumDetailsListOfArtistsUseCase,
//    private val getTopTrackDetailsListOfArtistsUseCase: GetTopTrackDetailsListOfArtistsUseCase,
    private val spotifyRepository: SpotifyRepository,
) : ViewModel() {
    private val artistId = Uri.decode(savedStateHandle.get<String>(DetailsSection.ARG_ARTIST_ID))

    private val dominantColor = MutableStateFlow(Color.Transparent)

    private val artistDetails = MutableStateFlow<ArtistDetails?>(null)
    private val albums = MutableStateFlow<List<AlbumDetails>>(emptyList())
    private val topTracks = MutableStateFlow<List<TrackDetails>>(emptyList())
    private val playlists = MutableStateFlow<List<PlaylistDetails>>(emptyList())
    private val otherArtists = MutableStateFlow<List<ArtistDetails>>(emptyList())
    private val isFavorite = MutableStateFlow(false)

    val state: StateFlow<ArtistDetailUiState> = combine(
        artistDetails,
        albums,
        topTracks,
        playlists,
        otherArtists,
        isFavorite,
        dominantColor,
    ) { artist, albums, tracks, playlists, others, isFav, color ->
        if (artist == null) return@combine ArtistDetailUiState.Loading

        ArtistDetailUiState.Ready(
            artist = ArtistUiModel.from(artist).copy(dominantColor = color),
            albums = albums.map { AlbumUiModel.from(it) },
            topTracks = tracks.map { TrackUiModel.from(it) },
            playlists = playlists.map { PlaylistUiModel.from(it) },
            otherArtists = others.map { ArtistUiModel.from(it) },
            isFavorite = isFav
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
            launch {
                spotifyRepository.getArtistDetails(artistId).let { artist ->
                    artistDetails.value = artist

                    artist.name.let { artistName ->
                        playlists.value =
                            spotifyRepository.searchPlaylistOfArtist(artistName) ?: emptyList()
                        otherArtists.value =
                            spotifyRepository.searchArtists(artistName)?.drop(1) ?: emptyList()
                    }
                }
            }
            launch {
                spotifyRepository.getAlbumsOfArtist(artistId).let {
                    albums.value = it.items
                }
            }
            launch {
                spotifyRepository.getTopTracksOfArtist(artistId).let {
                    topTracks.value = it
                }
            }
            launch {
                spotifyRepository.isFavorite(artistId, "artist").collectLatest {
                    isFavorite.value = it
                }
            }
        }
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intent.collectLatest { intent ->
                when (intent) {
                    is ArtistDetailUiIntent.PlayTracks -> playTracks()
                    is ArtistDetailUiIntent.ToggleFavorite -> toggleFavorite()
                    is ArtistDetailUiIntent.NavigateBack -> navigateBack()
                    is ArtistDetailUiIntent.NavigateToTrackDetail -> navigateToTrackDetail()
                    is ArtistDetailUiIntent.NavigateToAlbumDetail -> navigateToAlbumDetail(intent.albumId)
                    is ArtistDetailUiIntent.NavigateToArtistDetail -> navigateToArtistDetail(intent.artistId)
                    is ArtistDetailUiIntent.NavigateToPlaylistDetail -> navigateToPlaylistDetail(intent.playlistId)
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

    private fun navigateBack() {
        _effect.tryEmit(ArtistDetailUiEffect.NavigateBack)
    }

    private fun navigateToTrackDetail() {
        _effect.tryEmit(ArtistDetailUiEffect.NavigateToTrackDetail)
    }

    private fun navigateToAlbumDetail(albumId: String) {
        _effect.tryEmit(ArtistDetailUiEffect.NavigateToAlbumDetail(albumId))
    }

    private fun navigateToArtistDetail(artistId: String) {
        _effect.tryEmit(ArtistDetailUiEffect.NavigateToArtistDetail(artistId))
    }

    private fun navigateToPlaylistDetail(playlistId: String) {
        _effect.tryEmit(ArtistDetailUiEffect.NavigateToPlaylistDetail(playlistId))
    }

    private fun setDominantColor(color: Color) {
        dominantColor.value = color.darkenColor(0.5f)
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            if (!isFavorite.value) {
                spotifyRepository.insertFavorite(artistId, "artist")
            } else {
                spotifyRepository.deleteFavorite(artistId, "artist")
            }
        }
    }

    private fun playTracks() {
        topTracks.value.map { it.id }.let {
            spotifyRepository.playTracks(it)
        }
    }
}