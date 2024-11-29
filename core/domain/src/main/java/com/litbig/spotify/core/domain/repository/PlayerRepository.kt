package com.litbig.spotify.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
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
    fun setRepeat(mode: Int)
    fun addPlayList(path: String)
    fun addPlayLists(paths: List<String>)
    fun removePlayList(index: Int)
    fun clearPlayList()

    val mediaItems: Flow<List<String>>
    val currentMediaItem: Flow<String?>
    val currentPosition: Flow<Long>
    val playbackState: Flow<Int>
    val isPlaying: Flow<Boolean>
    val isShuffle: Flow<Boolean>
    val repeatMode: Flow<Int>
}