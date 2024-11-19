package com.litbig.spotify.ui.list

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.core.domain.usecase.GetMetadataByAlbumUseCase
import com.litbig.spotify.core.domain.usecase.GetMetadataByArtistUseCase
import com.litbig.spotify.core.domain.usecase.GetMetadataUseCase
import com.litbig.spotify.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    getMetadataUseCase: GetMetadataUseCase,
    getMetadataByAlbumUseCase: GetMetadataByAlbumUseCase,
    getMetadataByArtistUseCase: GetMetadataByArtistUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val decoded = Uri.decode(savedStateHandle.get<String>(Screen.ARG_MUSIC_INFO))
    val musicInfo = Json.decodeFromString<MusicInfo>(decoded)

    val metadataPagingFlow = when (musicInfo.category) {
        "album" -> getMetadataByAlbumUseCase(musicInfo.title, pageSize = 10)
            .cachedIn(viewModelScope)
        "artist" -> getMetadataByArtistUseCase(musicInfo.title, pageSize = 10)
            .cachedIn(viewModelScope)
        else -> getMetadataUseCase(pageSize = 10)
            .cachedIn(viewModelScope)
    }
}