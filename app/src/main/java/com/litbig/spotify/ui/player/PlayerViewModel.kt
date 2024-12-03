package com.litbig.spotify.ui.player

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.extension.combine
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.core.domain.repository.MusicRepository
import com.litbig.spotify.core.domain.repository.PlayerRepository
import com.litbig.spotify.core.domain.usecase.favorite.IsFavoriteUseCase
import com.litbig.spotify.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.litbig.spotify.util.darkenColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PlayerUiState {
    data object Idle : PlayerUiState
    data class Ready(
        val indexOfList: Int,
        val nowPlaying: MusicMetadata,
        val playList: List<MusicMetadata>,
        val playingTime: Long,
        val isPlaying: Boolean,
        val isShuffle: Boolean,
        val repeatMode: Int,
        val isFavorite: Boolean,
        val dominantColor: Color,
    ) : PlayerUiState
}

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val musicRepository: MusicRepository,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val playList = playerRepository.mediaItems.flatMapLatest { items ->
        combine(items.map { item ->
            musicRepository.getMetadataByAbsolutePath(item)
        }) { metadataList ->
            metadataList.toList()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val nowPlaying = playerRepository.currentMediaItem.flatMapLatest { item ->
        item?.let { musicRepository.getMetadataByAbsolutePath(it) } ?: flowOf(null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isFavorite = nowPlaying.flatMapLatest { metadata ->
        metadata?.let { isFavoriteUseCase.isFavoriteTrack(it.title) } ?: flowOf(false)
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


    private var _nowPlaying: MusicMetadata? = null
    private var _isPlaying = false
    private var _isShuffle = false
    private var _repeatMode = 0

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
                    imageUrl = it.albumArtUrl,
                )
            }
        }
    }

    fun onFavoriteIndex(index: Int) {
        viewModelScope.launch {
            playList.firstOrNull()?.getOrNull(index)?.let {
                toggleFavoriteUseCase.toggleFavoriteTrack(
                    trackName = it.title,
                    imageUrl = it.albumArtUrl,
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
}