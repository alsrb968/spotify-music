package com.litbig.spotify.ui.grid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.litbig.spotify.core.domain.usecase.GetAlbumsUseCase
import com.litbig.spotify.core.domain.usecase.GetMetadataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GridViewModel @Inject constructor(
    getMetadataUseCase: GetMetadataUseCase,
    getAlbumsUseCase: GetAlbumsUseCase,
) : ViewModel() {
    val musicMetadataPagingFlow = getMetadataUseCase(pageSize = 20)
        .cachedIn(viewModelScope)

    val musicAlbumsPagingFlow = getAlbumsUseCase()
        .cachedIn(viewModelScope)
}