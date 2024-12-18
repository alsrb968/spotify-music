package com.litbig.spotify.core.data.datasource.local

import android.net.Uri
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.io.File
import javax.inject.Inject

interface PlayerDataSource {
    fun play(path: String)
    fun play(paths: List<String>, indexToPlay: Int? = null)
    fun playIndex(index: Int)
    fun pause()
    fun resume()
    fun stop()
    fun next()
    fun previous()
    fun seekTo(position: Long)
    fun setShuffle(isShuffle: Boolean)
    fun setRepeat(repeatMode: Int)
    fun addPlayList(path: String)
    fun addPlayLists(paths: List<String>)
    fun removePlayList(index: Int)
    fun clearPlayList()
    fun release()

    val mediaItems: Flow<List<String>>
    val currentMediaItem: Flow<String?>
    val currentPosition: Flow<Long>
    val playbackState: Flow<Int>
    val isPlaying: Flow<Boolean>
    val isShuffle: Flow<Boolean>
    val repeatMode: Flow<Int>
}

class PlayerDataSourceImpl @Inject constructor(
    private val exoPlayer: ExoPlayer,
) : PlayerDataSource {

    private var positionUpdateJob: Job? = null

    private val _mediaItems = MutableStateFlow<List<String>>(emptyList())
    private val _currentMediaItem = MutableStateFlow<String?>(null)
    private val _currentPosition = MutableStateFlow(0L)
    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
    private val _isPlaying = MutableStateFlow(false)
    private val _isShuffle = MutableStateFlow(false)
    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)

    private val listener = object : Player.Listener {
        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            Timber.d(
                "onTimelineChanged " +
                        "timeline.periodCount=${timeline.periodCount}, " +
                        "timeline.windowCount=${timeline.windowCount}, " +
                        "reason: $reason"
            )

            when (reason) {
                Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED -> {
                    Timber.d("onTimelineChanged: TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED")
                }

                Player.TIMELINE_CHANGE_REASON_SOURCE_UPDATE -> {
                    Timber.d("onTimelineChanged: TIMELINE_CHANGE_REASON_SOURCE_UPDATE")
                }
            }

            val items = mutableListOf<MediaItem>()
            for (i in 0 until exoPlayer.mediaItemCount) {
                items.add(exoPlayer.getMediaItemAt(i))
            }
            _mediaItems.value = items.mapNotNull { item ->
                item.localConfiguration?.uri?.path
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            Timber.d("onMediaItemTransition uri: ${mediaItem?.localConfiguration?.uri}, reason: $reason, mediaId: ${mediaItem?.mediaId}")
            when (reason) {
                Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT -> {
                    Timber.d("onMediaItemTransition: MEDIA_ITEM_TRANSITION_REASON_REPEAT")
                }

                Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                    Timber.d("onMediaItemTransition: MEDIA_ITEM_TRANSITION_REASON_AUTO")
                }

                Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                    Timber.d("onMediaItemTransition: MEDIA_ITEM_TRANSITION_REASON_SEEK")
                }

                Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {
                    Timber.d("onMediaItemTransition: MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED")
                }
            }

            _currentMediaItem.value = mediaItem?.localConfiguration?.uri?.path
        }

        @UnstableApi
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            Timber.d(
                "onMediaMetadataChanged " +
                        "title=${mediaMetadata.title}, " +
                        "artist=${mediaMetadata.artist}, " +
                        "albumTitle=${mediaMetadata.albumTitle}, " +
                        "albumArtist=${mediaMetadata.albumArtist}, " +
                        "displayTitle=${mediaMetadata.displayTitle}, " +
                        "subTitle=${mediaMetadata.subtitle}, " +
                        "description=${mediaMetadata.description}, " +
                        "durationMs=${mediaMetadata.durationMs}, " +
                        "artworkData=${mediaMetadata.artworkData}, " +
                        "artworkDatType=${mediaMetadata.artworkDataType}, " +
                        "artworkUri=${mediaMetadata.artworkUri}, " +
                        "trackNumber=${mediaMetadata.trackNumber}, " +
                        "totalTrackCount=${mediaMetadata.totalTrackCount}"
            )
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _playbackState.value = playbackState
            when (playbackState) {
                Player.STATE_IDLE -> {
                    Timber.d("onPlaybackStateChanged: STATE_IDLE")
                }

                Player.STATE_BUFFERING -> {
                    Timber.d("onPlaybackStateChanged: STATE_BUFFERING")
                }

                Player.STATE_READY -> {
                    Timber.d("onPlaybackStateChanged: STATE_READY")
                }

                Player.STATE_ENDED -> {
                    Timber.d("onPlaybackStateChanged: STATE_ENDED")
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Timber.d("onIsPlayingChanged: $isPlaying")
            _isPlaying.value = isPlaying

            positionUpdateJob?.cancel()
            positionUpdateJob = CoroutineScope(Dispatchers.Main).launch {
                while (isPlaying) {
                    _currentPosition.value = exoPlayer.currentPosition
                    delay(500L)
                }
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            when (repeatMode) {
                Player.REPEAT_MODE_OFF -> {
                    Timber.d("onRepeatModeChanged: REPEAT_MODE_OFF")
                }

                Player.REPEAT_MODE_ONE -> {
                    Timber.d("onRepeatModeChanged: REPEAT_MODE_ONE")
                }

                Player.REPEAT_MODE_ALL -> {
                    Timber.d("onRepeatModeChanged: REPEAT_MODE_ALL")
                }
            }
            _repeatMode.value = repeatMode
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            Timber.d("onShuffleModeEnabledChanged: $shuffleModeEnabled")
            _isShuffle.value = shuffleModeEnabled
        }

        override fun onPlayerError(error: PlaybackException) {
            Timber.e("onPlayerError: $error")
        }
    }

    init {
        exoPlayer.addListener(listener)
    }

    override fun play(path: String) {
        val uri = Uri.fromFile(File(path))
        exoPlayer.playWhenReady = true
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
    }

    override fun play(paths: List<String>, indexToPlay: Int?) {
        val uris = paths.map { Uri.fromFile(File(it)) }
        exoPlayer.playWhenReady = true
        exoPlayer.setMediaItems(uris.map { MediaItem.fromUri(it) })
        indexToPlay?.let { exoPlayer.seekToDefaultPosition(it) }
        exoPlayer.prepare()
    }

    override fun playIndex(index: Int) {
        if (index in 0 until exoPlayer.mediaItemCount) {
            exoPlayer.playWhenReady = true
            exoPlayer.seekToDefaultPosition(index)
        }
    }

    override fun pause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        }
    }

    override fun resume() {
        if (!exoPlayer.isPlaying) {
            exoPlayer.play()
        }
    }

    override fun stop() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
    }

    override fun next() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.playWhenReady = true
            exoPlayer.seekToNextMediaItem()
        }
    }

    override fun previous() {
        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.playWhenReady = true
            exoPlayer.seekToPreviousMediaItem()
        }
    }

    override fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    override fun setShuffle(isShuffle: Boolean) {
        exoPlayer.shuffleModeEnabled = isShuffle
    }

    override fun setRepeat(repeatMode: Int) {
        exoPlayer.repeatMode = repeatMode
    }

    override fun addPlayList(path: String) {
        val uri = Uri.fromFile(File(path))
        exoPlayer.addMediaItem(MediaItem.fromUri(uri))
    }

    override fun addPlayLists(paths: List<String>) {
        val uris = paths.map { Uri.fromFile(File(it)) }
        exoPlayer.addMediaItems(uris.map { MediaItem.fromUri(it) })
    }

    override fun removePlayList(index: Int) {
        exoPlayer.removeMediaItem(index)
    }

    override fun clearPlayList() {
        exoPlayer.clearMediaItems()
    }

    override fun release() {
        exoPlayer.release()
        exoPlayer.removeListener(listener)
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
}

class FakePlayerDataSourceImpl @Inject constructor(

) : PlayerDataSource {

    private val _mediaItems = MutableStateFlow<List<String>>(emptyList())
    private val _currentMediaItem = MutableStateFlow<String?>(null)
    private val _currentPosition = MutableStateFlow(0L)

    /**
     * IDLE: 1
     * BUFFERING: 2
     * READY: 3
     * ENDED: 4
     */
    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
    private val _isPlaying = MutableStateFlow(false)
    private val _isShuffle = MutableStateFlow(false)

    /**
     * OFF: 0
     * ONE: 1
     * ALL: 2
     */
    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)

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
        }
    }

    private var positionUpdateJob: Job? = null

    private fun startDurationEncounter() {
        positionUpdateJob?.cancel()
        positionUpdateJob = CoroutineScope(Dispatchers.Main).launch {
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
        positionUpdateJob?.cancel()
    }

    override fun play(path: String) {
        _currentPosition.value = 0
        _mediaItems.value = listOf(path)
        _currentMediaItem.value = path
        _isPlaying.value = true
        _playbackState.value = Player.STATE_READY
    }

    override fun play(paths: List<String>, indexToPlay: Int?) {
        _currentPosition.value = 0
        _mediaItems.value = paths
        _currentMediaItem.value = paths.getOrNull(indexToPlay ?: 0)
        _isPlaying.value = true
        _playbackState.value = Player.STATE_READY
    }

    override fun playIndex(index: Int) {
        _currentPosition.value = 0
        _currentMediaItem.value = _mediaItems.value.getOrNull(index)
        _isPlaying.value = true
        _playbackState.value = Player.STATE_READY
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

    override fun release() {

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