package com.litbig.spotify.core.data.repository

import com.litbig.spotify.core.data.datasource.local.PlayerDataSource
import com.litbig.spotify.core.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val playerDataSource: PlayerDataSource,
) : PlayerRepository {
    override fun play(path: String) {
        playerDataSource.play(path)
    }

    override fun play(paths: List<String>, indexToPlay: Int?) {
        playerDataSource.play(paths, indexToPlay)
    }

    override fun playIndex(index: Int) {
        playerDataSource.playIndex(index)
    }

    override fun pause() {
        playerDataSource.pause()
    }

    override fun resume() {
        playerDataSource.resume()
    }

    override fun stop() {
        playerDataSource.stop()
    }

    override fun next() {
        playerDataSource.next()
    }

    override fun previous() {
        playerDataSource.previous()
    }

    override fun seekTo(position: Long) {
        playerDataSource.seekTo(position)
    }

    override fun setShuffle(isShuffle: Boolean) {
        playerDataSource.setShuffle(isShuffle)
    }

    override fun setRepeat(mode: Int) {
        playerDataSource.setRepeat(mode)
    }

    override fun addPlayList(path: String) {
        playerDataSource.addPlayList(path)
    }

    override fun addPlayLists(paths: List<String>) {
        playerDataSource.addPlayLists(paths)
    }

    override fun removePlayList(index: Int) {
        playerDataSource.removePlayList(index)
    }

    override fun clearPlayList() {
        playerDataSource.clearPlayList()
    }

    override val mediaItems: Flow<List<String>>
        get() = playerDataSource.mediaItems

    override val currentMediaItem: Flow<String?>
        get() = playerDataSource.currentMediaItem

    override val currentPosition: Flow<Long>
        get() = playerDataSource.currentPosition

    override val playbackState: Flow<Int>
        get() = playerDataSource.playbackState

    override val isPlaying: Flow<Boolean>
        get() = playerDataSource.isPlaying

    override val isShuffle: Flow<Boolean>
        get() = playerDataSource.isShuffle

    override val repeatMode: Flow<Int>
        get() = playerDataSource.repeatMode
}