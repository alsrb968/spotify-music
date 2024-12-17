package com.litbig.spotify.ui.player

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.data.di.RepositoryModule.FakePlayerRepository
import com.litbig.spotify.core.design.extension.darkenColor
import com.litbig.spotify.core.domain.extension.combine
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.repository.PlayerRepository
import com.litbig.spotify.core.domain.usecase.GetArtistDetailsUseCase
import com.litbig.spotify.core.domain.usecase.GetSeveralTrackDetailsUseCase
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
    object PlayOrPause : PlayerUiIntent
    object Next : PlayerUiIntent
    object Previous : PlayerUiIntent
    data class Progress(val position: Long) : PlayerUiIntent
    object Shuffle : PlayerUiIntent
    object Repeat : PlayerUiIntent
    object Favorite : PlayerUiIntent
    data class FavoriteIndex(val index: Int) : PlayerUiIntent
    data class SetDominantColor(val color: Color) : PlayerUiIntent
    data class ShowPlayer(val isShow: Boolean) : PlayerUiIntent
}

sealed interface PlayerUiEffect {
    data class ShowToast(val message: String) : PlayerUiEffect
}

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @FakePlayerRepository private val playerRepository: PlayerRepository,
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
        playerRepository.mediaItems,
        playerRepository.currentMediaItem,
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
        playerRepository.currentPosition,
        playerRepository.isPlaying,
        playerRepository.isShuffle,
        playerRepository.repeatMode,
        dominantColor,
    ) { currItem, items, artistDetails, currPos, isPlay, isShuf, repeat, domiColor ->
        if (currItem == null) {
            PlayerUiState.Idle
        } else {
            val index = items.indexOfFirst { it.id == currItem.id }
            val isFavorite = isFavoriteUseCase.isFavoriteTrack(currItem.id).first()

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
        playerRepository.playIndex(index)
    }

    private fun onPlayOrPause() {
        if (_isPlaying) {
            playerRepository.pause()
        } else {
            playerRepository.resume()
        }
    }

    private fun onNext() {
        playerRepository.next()
    }

    private fun onPrevious() {
        playerRepository.previous()
    }

    private fun onProgress(position: Long) {
        playerRepository.seekTo(position)
    }

    private fun onShuffle() {
        playerRepository.setShuffle(!_isShuffle)
    }

    private fun onRepeat() {
        playerRepository.setRepeat((_repeatMode + 1) % 3)
    }

    private fun onFavorite() {
        viewModelScope.launch {
            _nowPlaying?.let {
                toggleFavoriteUseCase.toggleFavoriteTrack(
                    trackName = it.name,
                    imageUrl = it.imageUrl,
                )
            }
        }
    }

    private fun onFavoriteIndex(index: Int) {
        viewModelScope.launch {
            _playList[index].let {
                toggleFavoriteUseCase.toggleFavoriteTrack(
                    trackName = it.name,
                    imageUrl = it.imageUrl,
                )
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