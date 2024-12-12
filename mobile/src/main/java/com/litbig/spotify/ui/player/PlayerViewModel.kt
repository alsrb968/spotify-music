@file:OptIn(ExperimentalCoroutinesApi::class)

package com.litbig.spotify.ui.player

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.data.di.RepositoryModule.MockingPlayerRepository
import com.litbig.spotify.core.design.extension.darkenColor
import com.litbig.spotify.core.domain.extension.combine
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.model.remote.TrackDetails
import com.litbig.spotify.core.domain.repository.PlayerRepository
import com.litbig.spotify.core.domain.usecase.GetArtistDetailsUseCase
import com.litbig.spotify.core.domain.usecase.GetTrackDetailsUseCase
import com.litbig.spotify.core.domain.usecase.favorite.IsFavoriteUseCase
import com.litbig.spotify.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.litbig.spotify.ui.home.album.TrackUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    ) : PlayerUiState
}

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @MockingPlayerRepository private val playerRepository: PlayerRepository,
    private val getTrackDetailsUseCase: GetTrackDetailsUseCase,
    private val getArtistDetailsUseCase: GetArtistDetailsUseCase,
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
                TrackUiModel(
                    id = track.id,
                    imageUrl = track.album?.images?.firstOrNull()?.url,
                    name = track.name,
                    artists = track.artists.joinToString { it.name },
                    duration = track.durationMs.toLong(),
                )
            }
        }
    }

    private val nowPlaying = playerRepository.currentMediaItem.flatMapLatest { item ->
        item?.let { trackDetails ->
            getTrackDetailsUseCase(trackDetails).map { track ->
                TrackUiModel(
                    id = track.id,
                    imageUrl = track.album?.images?.firstOrNull()?.url,
                    name = track.name,
                    artists = track.artists.joinToString { it.name },
                    duration = track.durationMs.toLong(),
                )
            }
        } ?: flowOf(null)
    }

    private val isFavorite = nowPlaying.flatMapLatest { metadata ->
        metadata?.let { isFavoriteUseCase.isFavoriteTrack(it.id) } ?: flowOf(false)
    }

    private val dominantColor = MutableStateFlow(Color.Transparent)

    val trackDetailInfo: Flow<TrackDetails?> = nowPlaying.flatMapLatest { trackDetails ->
        trackDetails?.let {
            getTrackDetailsUseCase(it.id)
        } ?: flowOf(null)
    }

    val artistDetailInfo: Flow<ArtistDetails?> = nowPlaying.flatMapLatest { trackDetails ->
        trackDetails?.let {
            getTrackDetailsUseCase(it.id).map { track ->
                track.artists.firstOrNull()?.id?.let { artistId ->
                    getArtistDetailsUseCase(artistId)
                }
            }
        } ?: flowOf(null)
    }

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

    private var _nowPlaying: TrackUiModel? = null
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
                    trackName = it.name,
                    imageUrl = it.imageUrl,
                )
            }
        }
    }

    fun onFavoriteIndex(index: Int) {
        viewModelScope.launch {
            playList.firstOrNull()?.getOrNull(index)?.let {
                toggleFavoriteUseCase.toggleFavoriteTrack(
                    trackName = it.name,
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