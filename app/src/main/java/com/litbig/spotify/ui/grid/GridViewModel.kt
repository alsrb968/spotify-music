package com.litbig.spotify.ui.grid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.litbig.spotify.core.domain.usecase.GetAlbumsUseCase
import com.litbig.spotify.core.domain.usecase.GetArtistsUseCase
import com.litbig.spotify.core.domain.usecase.favorite.GetFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GridViewModel @Inject constructor(
    getFavoritesUseCase: GetFavoritesUseCase,
    getAlbumsUseCase: GetAlbumsUseCase,
    getArtistsUseCase: GetArtistsUseCase,
) : ViewModel() {
    val favoritesPagingFlow = getFavoritesUseCase(pageSize = 10).cachedIn(viewModelScope)
    val albumsPagingFlow = getAlbumsUseCase(pageSize = 10).cachedIn(viewModelScope)
    val artistPagingFlow = getArtistsUseCase(pageSize = 10).cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            launch {
                favoritesPagingFlow.collectLatest {
                    Timber.e("favoriteMetadataPagingFlow.collectLatest")
                }
            }
            launch {
                albumsPagingFlow.collectLatest {
                    Timber.e("albumsPagingFlow.collectLatest")
                }
            }
            launch {
                artistPagingFlow.collectLatest {
                    Timber.e("artistPagingFlow.collectLatest")
                }
            }
        }
    }
}