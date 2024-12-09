package com.litbig.spotify.core.data.repository

import com.litbig.spotify.core.domain.repository.PlayerRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import javax.inject.Inject

class MockPlayerRepository @Inject constructor(

) : PlayerRepository {

    private val _mediaItems: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    private val _currentMediaItem: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _currentPosition: MutableStateFlow<Long> = MutableStateFlow(0)

    /**
     * IDLE: 1
     * BUFFERING: 2
     * READY: 3
     * ENDED: 4
     */
    private val _playbackState: MutableStateFlow<Int> = MutableStateFlow(1)
    private val _isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _isShuffle: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * OFF: 0
     * ONE: 1
     * ALL: 2
     */
    private val _repeatMode: MutableStateFlow<Int> = MutableStateFlow(0)

    private var durationEncounterJob: Job? = null

    init {
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                _isPlaying.collectLatest { isPlaying ->
                    if (isPlaying) {
                        startDurationEncounter()
                    } else {
                        stopDurationEncounter()
                    }
                }
            }

            launch {

            }
        }
    }

    private fun startDurationEncounter() {
        durationEncounterJob?.cancel()
        durationEncounterJob = CoroutineScope(Dispatchers.Main).launch {
            while (_isPlaying.value) {
                delay(500)
                _currentPosition.value += 500
                if (_currentPosition.value >= DURATION * 1000) {
                    val currentIndex = _mediaItems.value.indexOf(_currentMediaItem.value)
                    if (currentIndex == _mediaItems.value.size - 1) {
                        _isPlaying.value = false
                    } else {
                        next()
                    }
                }
            }
        }
    }

    private fun stopDurationEncounter() {
        durationEncounterJob?.cancel()
    }

    override fun play(path: String) {
        _currentPosition.value = 0
        _mediaItems.value = listOf(path)
        _currentMediaItem.value = path
        _isPlaying.value = true
        _playbackState.value = 3
    }

    override fun play(paths: List<String>, indexToPlay: Int?) {
        Timber.i("play paths: $paths, indexToPlay: $indexToPlay")
        _currentPosition.value = 0
        _mediaItems.value = paths
        _currentMediaItem.value = paths.getOrNull(indexToPlay ?: 0)
        _isPlaying.value = true
        _playbackState.value = 3
    }

    override fun playIndex(index: Int) {
        _currentPosition.value = 0
        _currentMediaItem.value = _mediaItems.value.getOrNull(index)
        _isPlaying.value = true
        _playbackState.value = 3
    }

    override fun pause() {
        _isPlaying.value = false
    }

    override fun resume() {
        _isPlaying.value = true
    }

    override fun stop() {
        _mediaItems.value = emptyList()
        _currentMediaItem.value = null
        _isPlaying.value = false
        _playbackState.value = 1
    }

    override fun next() {
        _currentPosition.value = 0
        val nextIndex = _mediaItems.value.indexOf(_currentMediaItem.value) + 1
        _currentMediaItem.value = _mediaItems.value.getOrNull(nextIndex)
    }

    override fun previous() {
        _currentPosition.value = 0
        val previousIndex = _mediaItems.value.indexOf(_currentMediaItem.value) - 1
        _currentMediaItem.value = _mediaItems.value.getOrNull(previousIndex)
    }

    override fun seekTo(position: Long) {
        if (position in 0 until DURATION * 1000) {
            _currentPosition.value = position
        }
    }

    override fun setShuffle(isShuffle: Boolean) {
        _isShuffle.value = isShuffle
    }

    override fun setRepeat(mode: Int) {
        _repeatMode.value = mode
    }

    override fun addPlayList(path: String) {
        _mediaItems.value += path
    }

    override fun addPlayLists(paths: List<String>) {
        _mediaItems.value += paths
    }

    override fun removePlayList(index: Int) {
        _mediaItems.value -= _mediaItems.value.getOrNull(index) ?: return
    }

    override fun clearPlayList() {
        _mediaItems.value = emptyList()
        _currentMediaItem.value = null
        _isPlaying.value = false
    }

    override val mediaItems: Flow<List<String>>
        get() = _mediaItems
    override val currentMediaItem: Flow<String?>
        get() = _currentMediaItem
    override val currentPosition: Flow<Long>
        get() = _currentPosition
    override val playbackState: Flow<Int>
        get() = _playbackState
    override val isPlaying: Flow<Boolean>
        get() = _isPlaying
    override val isShuffle: Flow<Boolean>
        get() = _isShuffle
    override val repeatMode: Flow<Int>
        get() = _repeatMode

    companion object {
        private const val DURATION = 192 // 3m 12s
    }
}