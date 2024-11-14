package com.litbig.spotify.ui.list

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.litbig.spotify.core.domain.usecase.GetMetadataByAlbumUseCase
import com.litbig.spotify.core.domain.usecase.GetMetadataUseCase
import com.litbig.spotify.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    getMetadataUseCase: GetMetadataUseCase,
    getMetadataByAlbumUseCase: GetMetadataByAlbumUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val albumName = Uri.decode(savedStateHandle.get<String>(Screen.ARG_ALBUM_NAME)!!)

    val musicMetadataPagingFlow = getMetadataUseCase(pageSize = 20)
        .cachedIn(viewModelScope)

    val musicMetadataByAlbumPagingFlow = getMetadataByAlbumUseCase(albumName, pageSize = 20)
        .cachedIn(viewModelScope)
}