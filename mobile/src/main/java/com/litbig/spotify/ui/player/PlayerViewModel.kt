package com.litbig.spotify.ui.player

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.design.extension.darkenColor
import com.litbig.spotify.core.domain.extension.combine
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import com.litbig.spotify.core.domain.usecase.spotify.GetArtistDetailsUseCase
import com.litbig.spotify.core.domain.usecase.spotify.GetSeveralTrackDetailsUseCase
import com.litbig.spotify.core.domain.usecase.favorite.IsFavoriteUseCase
import com.litbig.spotify.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.litbig.spotify.ui.models.ArtistUiModel
import com.litbig.spotify.ui.models.TrackUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PlayerUiState {
    data object Idle : PlayerUiState
    data class Ready(
        val indexOfList: Int,
        val nowPlaying: TrackUiModel,
        val playList: List<TrackUiModel>,
        val playingTime: Long,
        val isPlaying: Boolean,
        val isShuffle: Boolean,
        val repeatMode: Int,
        val isFavorite: Boolean,
        val dominantColor: Color,
        val artist: ArtistUiModel,
        val artistNames: List<String>,
    ) : PlayerUiState
}

sealed interface PlayerUiIntent {
    data class PlayIndex(val index: Int) : PlayerUiIntent
    data object PlayOrPause : PlayerUiIntent
    data object Next : PlayerUiIntent
    data object Previous : PlayerUiIntent
    data class Progress(val position: Long) : PlayerUiIntent
    data object Shuffle : PlayerUiIntent
    data object Repeat : PlayerUiIntent
    data object Favorite : PlayerUiIntent
    data class FavoriteIndex(val index: Int) : PlayerUiIntent
    data class SetDominantColor(val color: Color) : PlayerUiIntent
    data class ShowPlayer(val isShow: Boolean) : PlayerUiIntent
}

sealed interface PlayerUiEffect {
    data class ShowToast(val message: String) : PlayerUiEffect
}

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val spotifyRepository: SpotifyRepository,
    private val getSeveralTrackDetailsUseCase: GetSeveralTrackDetailsUseCase,
    private val getArtistDetailsUseCase: GetArtistDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
) : ViewModel() {

    private val _isShowPlayer: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isShowPlayer: StateFlow<Boolean> = _isShowPlayer

    private val currArtistDetails = MutableStateFlow<ArtistDetails?>(null)
    private val track = MutableStateFlow<TrackUiModel?>(null)

    private val trackList: Flow<List<TrackUiModel>> = combine(
        spotifyRepository.mediaItems,
        spotifyRepository.currentMediaItem,
    ) { items, item ->
        if (items.isEmpty()) return@combine emptyList()

        val trackDetailsList = getSeveralTrackDetailsUseCase(items.joinToString(","))

        if (item != null) {
            val index = items.indexOfFirst { it == item }
            val trackDetails = trackDetailsList.getOrNull(index)
            currArtistDetails.value = trackDetails?.artists?.firstOrNull()?.let {
                getArtistDetailsUseCase(it.id)
            }
            track.value = trackDetails?.let { TrackUiModel.from(it) }
        }

        trackDetailsList.map { TrackUiModel.from(it) }
    }

    private val dominantColor = MutableStateFlow(Color.Transparent)

    val state: StateFlow<PlayerUiState> = combine(
        track,
        trackList,
        currArtistDetails,
        spotifyRepository.currentPosition,
        spotifyRepository.isPlaying,
        spotifyRepository.isShuffle,
        spotifyRepository.repeatMode,
        dominantColor,
    ) { currItem, items, artistDetails, currPos, isPlay, isShuf, repeat, domiColor ->
        if (currItem == null) {
            PlayerUiState.Idle
        } else {
            val index = items.indexOfFirst { it.id == currItem.id }
            val isFavorite = isFavoriteUseCase.isFavoriteTrack(currItem.id).firstOrNull() ?: false

            if (artistDetails == null) {
                return@combine PlayerUiState.Idle
            }

            val artist = ArtistUiModel.from(artistDetails)
            val artistNames = currItem.artists.split(",")

            _nowPlaying = currItem
            _playList = items
            _isPlaying = isPlay
            _isShuffle = isShuf
            _repeatMode = repeat

            PlayerUiState.Ready(
                index,
                currItem,
                items,
                currPos,
                isPlay,
                isShuf,
                repeat,
                isFavorite,
                domiColor,
                artist,
                artistNames,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PlayerUiState.Idle
    )

    private val _intent = MutableSharedFlow<PlayerUiIntent>(extraBufferCapacity = 1)

    private val _effect = MutableSharedFlow<PlayerUiEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<PlayerUiEffect> = _effect

    private var _nowPlaying: TrackUiModel? = null
    private var _playList: List<TrackUiModel> = emptyList()
    private var _isPlaying = false
    private var _isShuffle = false
    private var _repeatMode = 0

    init {
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intent.collectLatest { intent ->
                when (intent) {
                    is PlayerUiIntent.PlayIndex -> onPlayIndex(intent.index)
                    is PlayerUiIntent.PlayOrPause -> onPlayOrPause()
                    is PlayerUiIntent.Next -> onNext()
                    is PlayerUiIntent.Previous -> onPrevious()
                    is PlayerUiIntent.Progress -> onProgress(intent.position)
                    is PlayerUiIntent.Shuffle -> onShuffle()
                    is PlayerUiIntent.Repeat -> onRepeat()
                    is PlayerUiIntent.Favorite -> onFavorite()
                    is PlayerUiIntent.FavoriteIndex -> onFavoriteIndex(intent.index)
                    is PlayerUiIntent.SetDominantColor -> setDominantColor(intent.color)
                    is PlayerUiIntent.ShowPlayer -> showPlayer(intent.isShow)
                }
            }
        }
    }

    fun sendIntent(intent: PlayerUiIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private fun onPlayIndex(index: Int) {
        spotifyRepository.playIndex(index)
    }

    private fun onPlayOrPause() {
        spotifyRepository.playOrPause()
    }

    private fun onNext() {
        spotifyRepository.next()
    }

    private fun onPrevious() {
        spotifyRepository.previous()
    }

    private fun onProgress(position: Long) {
        spotifyRepository.seekTo(position)
    }

    private fun onShuffle() {
        spotifyRepository.setShuffle(!_isShuffle)
    }

    private fun onRepeat() {
        spotifyRepository.setRepeat((_repeatMode + 1) % 3)
    }

    private fun onFavorite() {
        viewModelScope.launch {
            _nowPlaying?.let {
                if (toggleFavoriteUseCase.toggleFavoriteTrack(it.id, null)) {
                    _effect.emit(PlayerUiEffect.ShowToast("Added to favorite"))
                } else {
                    _effect.emit(PlayerUiEffect.ShowToast("Removed from favorite"))
                }
            }
        }
    }

    private fun onFavoriteIndex(index: Int) {
        viewModelScope.launch {
            _playList[index].let {
                if (toggleFavoriteUseCase.toggleFavoriteTrack(it.id, null)) {
                    _effect.emit(PlayerUiEffect.ShowToast("Added to favorite"))
                } else {
                    _effect.emit(PlayerUiEffect.ShowToast("Removed from favorite"))
                }
            }
        }
    }

    private fun isFavoriteTrack(trackName: String): Flow<Boolean> {
        return isFavoriteUseCase.isFavoriteTrack(trackName)
    }

    private fun setDominantColor(color: Color) {
        dominantColor.value = color.darkenColor(0.5f)
    }

    private fun showPlayer(isShow: Boolean) {
        _isShowPlayer.value = isShow
    }
}