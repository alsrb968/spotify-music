package com.litbig.spotify.ui.home.album

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.data.di.RepositoryModule.FakePlayerRepository
import com.litbig.spotify.core.design.extension.darkenColor
import com.litbig.spotify.core.domain.repository.PlayerRepository
import com.litbig.spotify.core.domain.usecase.GetAlbumDetailsUseCase
import com.litbig.spotify.core.domain.usecase.favorite.IsFavoriteUseCase
import com.litbig.spotify.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.litbig.spotify.ui.home.HomeSection
import com.litbig.spotify.ui.models.AlbumUiModel
import com.litbig.spotify.ui.models.TrackUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface AlbumDetailUiState {
    data object Loading : AlbumDetailUiState
    data class Ready(
        val album: AlbumUiModel,
        val tracks: List<TrackUiModel>?,
        val playingTrackId: String?,
    ) : AlbumDetailUiState
}

sealed interface AlbumDetailUiIntent {
    data class PlayTrack(val trackId: String) : AlbumDetailUiIntent
    data class PlayTracks(val trackIds: List<String>) : AlbumDetailUiIntent
    data class AddTrack(val trackId: String) : AlbumDetailUiIntent
    data class AddTracks(val trackIds: List<String>) : AlbumDetailUiIntent
    data class ToggleFavoriteAlbum(val albumId: String) : AlbumDetailUiIntent
    data class ToggleFavoriteTrack(val trackId: String) : AlbumDetailUiIntent
    data class SetDominantColor(val color: Color) : AlbumDetailUiIntent
}

sealed interface AlbumDetailUiEffect {
    data class ShowToast(val message: String) : AlbumDetailUiEffect
}

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAlbumDetailsUseCase: GetAlbumDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    @FakePlayerRepository private val playerRepository: PlayerRepository
) : ViewModel() {
    private val albumId = Uri.decode(savedStateHandle.get<String>(HomeSection.ARG_ALBUM_ID))

    private val dominantColor = MutableStateFlow(Color.Transparent)

    val state: StateFlow<AlbumDetailUiState> = combine(
        getAlbumDetailsUseCase(albumId),
        dominantColor,
        playerRepository.currentMediaItem,
    ) { albumDetails, color, currentItem ->
        val imageUrl = albumDetails.images.firstOrNull()?.url
        AlbumDetailUiState.Ready(
            album = AlbumUiModel.from(albumDetails).copy(dominantColor = color),
            tracks = albumDetails.tracks?.items?.map {
                TrackUiModel.from(it).copy(imageUrl = imageUrl)
            },
            playingTrackId = currentItem
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = AlbumDetailUiState.Loading
    )

    private val _intent = MutableSharedFlow<AlbumDetailUiIntent>(extraBufferCapacity = 1)

    private val _effect = MutableSharedFlow<AlbumDetailUiEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<AlbumDetailUiEffect> = _effect

    init {
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intent.collectLatest { intent ->
                when (intent) {
                    is AlbumDetailUiIntent.PlayTrack -> playTrack(intent.trackId)
                    is AlbumDetailUiIntent.PlayTracks -> playTracks(intent.trackIds)
                    is AlbumDetailUiIntent.AddTrack -> addPlaylist(intent.trackId)
                    is AlbumDetailUiIntent.AddTracks -> addPlaylists(intent.trackIds)
                    is AlbumDetailUiIntent.ToggleFavoriteAlbum -> toggleFavoriteAlbum(intent.albumId)
                    is AlbumDetailUiIntent.ToggleFavoriteTrack -> toggleFavoriteTrack(intent.trackId)
                    is AlbumDetailUiIntent.SetDominantColor -> setDominantColor(intent.color)
                }
            }
        }
    }

    fun sendIntent(intent: AlbumDetailUiIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    fun isFavoriteTrack(trackId: String): Flow<Boolean> {
        return isFavoriteUseCase.isFavoriteTrack(trackId)
    }

    fun isFavoriteAlbum(albumId: String): Flow<Boolean> {
        return isFavoriteUseCase.isFavoriteAlbum(albumId)
    }

    private fun playTrack(trackId: String) {
        Timber.w("play trackId: $trackId")
        playerRepository.play(trackId)
    }

    private fun playTracks(trackIdList: List<String>) {
        Timber.w("play trackIdList: $trackIdList")
        playerRepository.play(trackIdList)
    }

    private fun addPlaylist(trackId: String) {
        playerRepository.addPlayList(trackId)
    }

    private fun addPlaylists(trackIdList: List<String>) {
        playerRepository.addPlayLists(trackIdList)
    }

    private fun toggleFavoriteAlbum(albumId: String) {
        viewModelScope.launch {
            if (toggleFavoriteUseCase.toggleFavoriteAlbum(albumId, null)) {
                _effect.emit(AlbumDetailUiEffect.ShowToast("Added to favorite"))
            } else {
                _effect.emit(AlbumDetailUiEffect.ShowToast("Removed from favorite"))
            }
        }
    }

    private fun toggleFavoriteTrack(trackId: String) {
        viewModelScope.launch {
            if (toggleFavoriteUseCase.toggleFavoriteTrack(trackId, null)) {
                _effect.emit(AlbumDetailUiEffect.ShowToast("Added to favorite"))
            } else {
                _effect.emit(AlbumDetailUiEffect.ShowToast("Removed from favorite"))
            }
        }
    }

    private fun setDominantColor(color: Color) {
        dominantColor.value = color.darkenColor(0.5f)
    }
}