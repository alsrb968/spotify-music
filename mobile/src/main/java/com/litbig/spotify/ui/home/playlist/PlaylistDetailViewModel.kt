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
    ) : PlaylistDetailUiState
}

sealed interface PlaylistDetailUiIntent {
    data object PlayTracks : PlaylistDetailUiIntent
    data object ShowMore : PlaylistDetailUiIntent
    data class SelectPlaylist(val playlistId: String) : PlaylistDetailUiIntent
    data class SetDominantColor(val color: Color) : PlaylistDetailUiIntent
}

sealed interface PlaylistDetailUiEffect {
    data class NavigateToPlaylistDetail(val playlistId: String) : PlaylistDetailUiEffect
    data class ShowToast(val message: String) : PlaylistDetailUiEffect
}

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val spotifyRepository: SpotifyRepository,
) : ViewModel() {
    private val playlistId: String = Uri.decode(savedStateHandle.get<String>(HomeSection.ARG_PLAYLIST_ID))

    private val dominantColor = MutableStateFlow(Color.Transparent)

    private val playlistDetails = MutableStateFlow<PlaylistDetails?>(null)
    private val owner = MutableStateFlow<OwnerUiModel?>(null)
    private val otherPlaylists = MutableStateFlow<List<PlaylistUiModel>>(emptyList())

    val state: StateFlow<PlaylistDetailUiState> = combine(
        playlistDetails,
        otherPlaylists,
        owner,
        dominantColor,
    ) { playlist, otherPlaylists, owner, color ->
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
                            otherPlaylists.value = playlists?.map { PlaylistUiModel.from(it) } ?: emptyList()
                        }
                    }
                }
            }
        }

        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intent.collectLatest { intent ->
                when (intent) {
                    is PlaylistDetailUiIntent.PlayTracks -> playTracks()
                    is PlaylistDetailUiIntent.ShowMore -> showMore()
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

    private fun playTracks() {
        // todo
        _effect.tryEmit(PlaylistDetailUiEffect.ShowToast("PlayTracks"))
    }

    private fun showMore() {
        // todo
        _effect.tryEmit(PlaylistDetailUiEffect.ShowToast("ShowMore"))
    }

    private fun selectPlaylist(playlistId: String) {
        _effect.tryEmit(PlaylistDetailUiEffect.NavigateToPlaylistDetail(playlistId))
    }

    private fun setDominantColor(color: Color) {
        dominantColor.value = color.darkenColor(0.5f)
    }
}