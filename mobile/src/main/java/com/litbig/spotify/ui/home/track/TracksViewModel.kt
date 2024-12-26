package com.litbig.spotify.ui.home.track

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.model.remote.PlaylistDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import com.litbig.spotify.ui.home.HomeSection
import com.litbig.spotify.ui.models.TrackUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TracksUiState {
    data object Loading : TracksUiState
    data class Ready(
        val imageUrl: String?,
        val title: String,
        val tracks: List<TrackUiModel>,
        val dominantColor: Color,
    ) : TracksUiState
}

sealed interface TracksUiIntent {
    data object NavigateBack : TracksUiIntent
    data class NavigateToArtist(val artistId: String) : TracksUiIntent
    data class SelectTrack(val trackId: String) : TracksUiIntent
    data class SetDominantColor(val color: Color) : TracksUiIntent
}

sealed interface TracksUiEffect {
    data object NavigateBack : TracksUiEffect
    data class NavigateToArtist(val artistId: String) : TracksUiEffect
    data class ShowToast(val message: String) : TracksUiEffect
}

@HiltViewModel
class TracksViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val spotifyRepository: SpotifyRepository
) : ViewModel() {
    private var playlistId: String = Uri.decode(savedStateHandle.get<String>(HomeSection.ARG_PLAYLIST_ID))

    private val playlistDetails = MutableStateFlow<PlaylistDetails?>(null)
    private val dominantColor = MutableStateFlow(Color.Transparent)

    val state: StateFlow<TracksUiState> = combine(
        playlistDetails,
        dominantColor,
    ) { playlist, color ->
        if (playlist == null) return@combine TracksUiState.Loading

        TracksUiState.Ready(
            imageUrl = playlist.images.firstOrNull()?.url,
            title = playlist.name,
            tracks = playlist.tracks.items?.map { track ->
                TrackUiModel(
                    id = track.track.id,
                    imageUrl = track.track.album?.images?.firstOrNull()?.url,
                    name = track.track.name,
                    artists = track.track.artists.joinToString { it.name },
                    duration = track.track.durationMs.toLong(),
                )
            } ?: emptyList(),
            dominantColor = color
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TracksUiState.Loading
    )

    private val _intent = MutableSharedFlow<TracksUiIntent>(extraBufferCapacity = 1)

    private val _effect = MutableSharedFlow<TracksUiEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<TracksUiEffect> = _effect

    init {
        viewModelScope.launch {
            launch {
                playlistDetails.value = spotifyRepository.getPlaylistDetails(playlistId)
            }
        }
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intent.collectLatest { intent ->
                when (intent) {
                    is TracksUiIntent.NavigateToArtist -> navigateToArtist(intent.artistId)
                    is TracksUiIntent.NavigateBack -> navigateBack()
                    is TracksUiIntent.SelectTrack -> showToast("Track selected: ${intent.trackId}")
                    is TracksUiIntent.SetDominantColor -> setDominantColor(intent.color)
                }
            }
        }
    }

    fun sendIntent(intent: TracksUiIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _effect.emit(TracksUiEffect.NavigateBack)
        }
    }

    private fun navigateToArtist(artistId: String) {
        viewModelScope.launch {
            _effect.emit(TracksUiEffect.NavigateToArtist(artistId))
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _effect.emit(TracksUiEffect.ShowToast(message))
        }
    }

    private fun setDominantColor(color: Color) {
        dominantColor.value = color
    }
}