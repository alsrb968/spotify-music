package com.litbig.spotify.ui.list

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.litbig.spotify.core.domain.usecase.GetMetadataByAlbumUseCase
import com.litbig.spotify.core.domain.usecase.GetMetadataByArtistUseCase
import com.litbig.spotify.core.domain.usecase.GetMetadataUseCase
import com.litbig.spotify.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    getMetadataUseCase: GetMetadataUseCase,
    getMetadataByAlbumUseCase: GetMetadataByAlbumUseCase,
    getMetadataByArtistUseCase: GetMetadataByArtistUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val arguments = Uri.decode(savedStateHandle.get<String>(Screen.ARG_CATEGORY)!!).split("/")
    private val category = arguments[0]
    val name = if (arguments.size > 1) {
        arguments.subList(1, arguments.size).joinToString("/")
    } else {
        ""
    }

    val metadataPagingFlow = when (category) {
        "album" -> getMetadataByAlbumUseCase(name, pageSize = 10)
            .cachedIn(viewModelScope)
        "artist" -> getMetadataByArtistUseCase(name, pageSize = 10)
            .cachedIn(viewModelScope)
        else -> getMetadataUseCase(pageSize = 10)
            .cachedIn(viewModelScope)
    }
}