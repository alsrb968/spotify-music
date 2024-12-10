@file:OptIn(ExperimentalCoroutinesApi::class)

package com.litbig.spotify.ui.player

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.data.di.RepositoryModule.MockingPlayerRepository
import com.litbig.spotify.core.design.extension.darkenColor
import com.litbig.spotify.core.domain.extension.combine
import com.litbig.spotify.core.domain.repository.PlayerRepository
import com.litbig.spotify.core.domain.usecase.GetTrackDetailsUseCase
import com.litbig.spotify.core.domain.usecase.favorite.IsFavoriteUseCase
import com.litbig.spotify.core.domain.usecase.favorite.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface PlayerUiState {
    data object Idle : PlayerUiState
    data class Ready(
        val indexOfList: Int,
        val nowPlaying: TrackDetailsInfo,
        val playList: List<TrackDetailsInfo>,
        val playingTime: Long,
        val isPlaying: Boolean,
        val isShuffle: Boolean,
        val repeatMode: Int,
        val isFavorite: Boolean,
        val dominantColor: Color,
    ) : PlayerUiState
}

data class TrackDetailsInfo(
    val id: String,
    val imageUrl: String?,
    val title: String,
    val artist: String,
    val duration: Long,
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @MockingPlayerRepository private val playerRepository: PlayerRepository,
    private val getTrackDetailsUseCase: GetTrackDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
) : ViewModel() {

    private val _isShowPlayer: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isShowPlayer: StateFlow<Boolean> = _isShowPlayer

    private val playList = playerRepository.mediaItems.flatMapLatest { items ->
        combine(items.map { item ->
            getTrackDetailsUseCase(item)
        }) { trackDetails ->
            trackDetails.map { track ->
                TrackDetailsInfo(
                    id = track.id,
                    imageUrl = track.album?.images?.firstOrNull()?.url,
                    title = track.name,
                    artist = track.artists.joinToString { it.name },
                    duration = track.durationMs.toLong(),
                )
            }
        }
    }

    private val nowPlaying = playerRepository.currentMediaItem.flatMapLatest { item ->
        item?.let { trackDetails ->
            getTrackDetailsUseCase(trackDetails).map {
                TrackDetailsInfo(
                    id = it.id,
                    imageUrl = it.album?.images?.firstOrNull()?.url,
                    title = it.name,
                    artist = it.artists.joinToString { artist -> artist.name },
                    duration = it.durationMs.toLong(),
                )
            }
        } ?: flowOf(null)
    }

    private val isFavorite = nowPlaying.flatMapLatest { metadata ->
        metadata?.let { isFavoriteUseCase.isFavoriteTrack(it.id) } ?: flowOf(false)
    }

    private val dominantColor = MutableStateFlow(Color.Transparent)

    val state: StateFlow<PlayerUiState> = combine(
        nowPlaying,
        playList,
        playerRepository.currentPosition,
        playerRepository.isPlaying,
        playerRepository.isShuffle,
        playerRepository.repeatMode,
        isFavorite,
        dominantColor,
    ) { nowPlaying, playList, playingTime, isPlaying, isShuffle, repeatMode, isFavorite, color ->
//        Timber.i("nowPlaying: $nowPlaying, playingTime: $playingTime, isPlaying: $isPlaying, isShuffle: $isShuffle, repeatMode: $repeatMode, isFavorite: $isFavorite, color: $color")
        if (nowPlaying == null) {
            PlayerUiState.Idle
        } else {
            val index = playList.indexOf(nowPlaying)

            _nowPlaying = nowPlaying
            _isPlaying = isPlaying
            _isShuffle = isShuffle
            _repeatMode = repeatMode

            PlayerUiState.Ready(
                index,
                nowPlaying,
                playList,
                playingTime,
                isPlaying,
                isShuffle,
                repeatMode,
                isFavorite,
                color,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PlayerUiState.Idle
    )

    private var _nowPlaying: TrackDetailsInfo? = null
    private var _isPlaying = false
    private var _isShuffle = false
    private var _repeatMode = 0

    init {
        viewModelScope.launch {
            _isShowPlayer.collectLatest {
                Timber.i("isShowPlayer: $it")
            }
        }
    }

    fun onPlayIndex(index: Int) {
        playerRepository.playIndex(index)
    }

    fun onPlayOrPause() {
        if (_isPlaying) {
            playerRepository.pause()
        } else {
            playerRepository.resume()
        }
    }

    fun onNext() {
        playerRepository.next()
    }

    fun onPrevious() {
        playerRepository.previous()
    }

    fun onProgress(position: Long) {
        playerRepository.seekTo(position)
    }

    fun onShuffle() {
        playerRepository.setShuffle(!_isShuffle)
    }

    fun onRepeat() {
        playerRepository.setRepeat((_repeatMode + 1) % 3)
    }

    fun onFavorite() {
        viewModelScope.launch {
            _nowPlaying?.let {
                toggleFavoriteUseCase.toggleFavoriteTrack(
                    trackName = it.title,
                    imageUrl = it.imageUrl,
                )
            }
        }
    }

    fun onFavoriteIndex(index: Int) {
        viewModelScope.launch {
            playList.firstOrNull()?.getOrNull(index)?.let {
                toggleFavoriteUseCase.toggleFavoriteTrack(
                    trackName = it.title,
                    imageUrl = it.imageUrl,
                )
            }
        }
    }

    fun isFavoriteTrack(trackName: String): Flow<Boolean> {
        return isFavoriteUseCase.isFavoriteTrack(trackName)
    }

    fun setDominantColor(color: Color) {
        dominantColor.value = color.darkenColor(0.5f)
    }
    
    fun showPlayer(isShow: Boolean) {
        _isShowPlayer.value = isShow
    }
}