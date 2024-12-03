package com.litbig.spotify.ui.list

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import com.litbig.spotify.core.domain.repository.PlayerRepository
import com.litbig.spotify.core.domain.usecase.metadata.GetMetadataByAlbumUseCase
import com.litbig.spotify.core.domain.usecase.metadata.GetMetadataByArtistUseCase
import com.litbig.spotify.core.domain.usecase.metadata.GetMetadataUseCase
import com.litbig.spotify.core.domain.usecase.favorite.IsFavoriteUseCase
import com.litbig.spotify.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.litbig.spotify.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMetadataUseCase: GetMetadataUseCase,
    getMetadataByAlbumUseCase: GetMetadataByAlbumUseCase,
    getMetadataByArtistUseCase: GetMetadataByArtistUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val playerRepository: PlayerRepository,
) : ViewModel() {
    private val decoded = Uri.decode(savedStateHandle.get<String>(Screen.ARG_MUSIC_INFO))
    val musicInfo = Json.decodeFromString<MusicInfo>(decoded)

    val metadataPagingFlow = when (musicInfo.category) {
        "album" -> getMetadataByAlbumUseCase(musicInfo.title, pageSize = 10)
            .cachedIn(viewModelScope)

        "artist" -> getMetadataByArtistUseCase(musicInfo.title, pageSize = 10)
            .cachedIn(viewModelScope)

        else -> getMetadataUseCase(pageSize = 10)
            .cachedIn(viewModelScope)
    }

    fun isFavoriteTrack(trackName: String): Flow<Boolean> {
        return isFavoriteUseCase.isFavoriteTrack(trackName)
    }

    fun toggleFavoriteTrack(trackName: String, imageUrl: String? = null) {
        viewModelScope.launch {
            toggleFavoriteUseCase.toggleFavoriteTrack(trackName, imageUrl)
        }
    }

    fun isFavoriteAlbum(albumName: String): Flow<Boolean> {
        return isFavoriteUseCase.isFavoriteAlbum(albumName)
    }

    fun toggleFavoriteAlbum(albumName: String, imageUrl: String? = null) {
        viewModelScope.launch {
            toggleFavoriteUseCase.toggleFavoriteAlbum(albumName, imageUrl)
        }
    }

    fun isFavoriteArtist(artistName: String): Flow<Boolean> {
        return isFavoriteUseCase.isFavoriteArtist(artistName)
    }

    fun toggleFavoriteArtist(artistName: String, imageUrl: String? = null) {
        viewModelScope.launch {
            toggleFavoriteUseCase.toggleFavoriteArtist(artistName, imageUrl)
        }
    }

    fun play(metadata: MusicMetadata) {
        Timber.w("play metadata: $metadata")
        playerRepository.play(metadata.absolutePath)
    }

    fun play(metadataList: List<MusicMetadata>) {
        Timber.w("play metadataList: $metadataList")
        playerRepository.play(metadataList.map { it.absolutePath })
    }

    fun addPlaylist(metadataList: List<MusicMetadata>) {
        playerRepository.addPlayLists(metadataList.map { it.absolutePath })
    }
}