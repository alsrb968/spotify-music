package com.litbig.spotify.ui.home.playlist

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.design.extension.darkenColor
import com.litbig.spotify.core.domain.model.remote.PlaylistDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import com.litbig.spotify.ui.home.HomeSection
import com.litbig.spotify.ui.models.OwnerUiModel
import com.litbig.spotify.ui.models.PlaylistUiModel
import com.litbig.spotify.ui.models.TrackUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PlaylistDetailUiState {
    data object Loading : PlaylistDetailUiState
    data class Ready(
        val playlist: PlaylistUiModel,
        val tracks: List<TrackUiModel>,
        val otherPlaylists: List<PlaylistUiModel>,
        val owner: OwnerUiModel,
        val isFavorite: Boolean,
    ) : PlaylistDetailUiState
}

sealed interface PlaylistDetailUiIntent {
    data object Back : PlaylistDetailUiIntent
    data object PlayTracks : PlaylistDetailUiIntent
    data object ShowMore : PlaylistDetailUiIntent
    data object DownloadTracks : PlaylistDetailUiIntent
    data object ToggleFavorite : PlaylistDetailUiIntent
    data object SelectTracks : PlaylistDetailUiIntent
    data object SelectOwner : PlaylistDetailUiIntent
    data class SelectPlaylist(val playlistId: String) : PlaylistDetailUiIntent
    data class SetDominantColor(val color: Color) : PlaylistDetailUiIntent
}

sealed interface PlaylistDetailUiEffect {
    data object NavigateBack : PlaylistDetailUiEffect
    data class NavigateToPlaylistDetail(val playlistId: String) : PlaylistDetailUiEffect
    data class NavigateToOwnerDetail(val ownerId: String) : PlaylistDetailUiEffect
    data class NavigateToTracks(val playlistId: String) : PlaylistDetailUiEffect
    data class ShowToast(val message: String) : PlaylistDetailUiEffect
}

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val spotifyRepository: SpotifyRepository,
) : ViewModel() {
    private val playlistId: String =
        Uri.decode(savedStateHandle.get<String>(HomeSection.ARG_PLAYLIST_ID))

    private val dominantColor = MutableStateFlow(Color.Transparent)

    private val playlistDetails = MutableStateFlow<PlaylistDetails?>(null)
    private val owner = MutableStateFlow<OwnerUiModel?>(null)
    private val otherPlaylists = MutableStateFlow<List<PlaylistUiModel>>(emptyList())
    private val isFavorite = MutableStateFlow(false)

    val state: StateFlow<PlaylistDetailUiState> = combine(
        playlistDetails,
        otherPlaylists,
        owner,
        isFavorite,
        dominantColor,
    ) { playlist, otherPlaylists, owner, isFavorite, color ->
        if (playlist == null) {
            return@combine PlaylistDetailUiState.Loading
        }
        if (owner == null) {
            return@combine PlaylistDetailUiState.Loading
        }

        PlaylistDetailUiState.Ready(
            playlist = PlaylistUiModel.from(playlist).copy(dominantColor = color),
            tracks = playlist.tracks.items?.map { TrackUiModel.from(it.track) } ?: emptyList(),
            otherPlaylists = otherPlaylists,
            owner = owner,
            isFavorite = isFavorite,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PlaylistDetailUiState.Loading,
    )

    private val _intent = MutableSharedFlow<PlaylistDetailUiIntent>(extraBufferCapacity = 1)

    private val _effect = MutableSharedFlow<PlaylistDetailUiEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<PlaylistDetailUiEffect> = _effect

    init {
        viewModelScope.launch {
            launch {
                spotifyRepository.getPlaylistDetails(playlistId).let { playlist ->
                    playlistDetails.value = playlist

                    val ownerId = playlist.owner.id
                    owner.value = OwnerUiModel.from(spotifyRepository.getUserProfile(ownerId))

                    playlist.tracks.items?.firstOrNull()?.track?.artists?.firstOrNull()?.name?.let { artist ->
                        spotifyRepository.searchPlaylistOfArtist(artist).let { playlists ->
                            otherPlaylists.value =
                                playlists?.map { PlaylistUiModel.from(it) } ?: emptyList()
                        }
                    }
                }
            }
            launch {
                spotifyRepository.isFavorite(playlistId, "playlist").collectLatest { favorite ->
                    isFavorite.value = favorite
                }
            }
        }

        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intent.collectLatest { intent ->
                when (intent) {
                    is PlaylistDetailUiIntent.Back -> navigateBack()
                    is PlaylistDetailUiIntent.PlayTracks -> playTracks()
                    is PlaylistDetailUiIntent.ShowMore -> showMore()
                    is PlaylistDetailUiIntent.DownloadTracks -> downloadTracks()
                    is PlaylistDetailUiIntent.ToggleFavorite -> toggleFavorite()
                    is PlaylistDetailUiIntent.SelectTracks -> selectTracks()
                    is PlaylistDetailUiIntent.SelectOwner -> selectOwner()
                    is PlaylistDetailUiIntent.SelectPlaylist -> selectPlaylist(intent.playlistId)
                    is PlaylistDetailUiIntent.SetDominantColor -> setDominantColor(intent.color)
                }
            }
        }
    }

    fun sendIntent(intent: PlaylistDetailUiIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private fun navigateBack() {
        _effect.tryEmit(PlaylistDetailUiEffect.NavigateBack)
    }

    private fun playTracks() {
        playlistDetails.value?.tracks?.items?.map { it.track.id }?.let {
            spotifyRepository.playTracks(it)
        }
    }

    private fun showMore() {
        // todo
        _effect.tryEmit(PlaylistDetailUiEffect.ShowToast("ShowMore"))
    }

    private fun downloadTracks() {
        // todo
        _effect.tryEmit(PlaylistDetailUiEffect.ShowToast("DownloadTracks"))
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            if (!isFavorite.value) {
                spotifyRepository.insertFavorite(playlistId, "playlist")
            } else {
                spotifyRepository.deleteFavorite(playlistId, "playlist")
            }
        }
    }

    private fun selectOwner() {
        owner.value?.id?.let { ownerId ->
            _effect.tryEmit(PlaylistDetailUiEffect.NavigateToOwnerDetail(ownerId))
        }
    }

    private fun selectPlaylist(playlistId: String) {
        _effect.tryEmit(PlaylistDetailUiEffect.NavigateToPlaylistDetail(playlistId))
    }

    private fun selectTracks() {
        _effect.tryEmit(PlaylistDetailUiEffect.NavigateToTracks(playlistId))
    }

    private fun setDominantColor(color: Color) {
        dominantColor.value = color.darkenColor(0.5f)
    }
}