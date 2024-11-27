package com.litbig.spotify.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.litbig.spotify.core.domain.usecase.GetAlbumsUseCase
import com.litbig.spotify.core.domain.usecase.GetArtistsUseCase
import com.litbig.spotify.core.domain.usecase.favorite.GetFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getFavoritesUseCase: GetFavoritesUseCase,
    getAlbumsUseCase: GetAlbumsUseCase,
    getArtistsUseCase: GetArtistsUseCase,
) : ViewModel() {
    val favoritesPagingFlow = getFavoritesUseCase(pageSize = 10).cachedIn(viewModelScope)
    val albumsPagingFlow = getAlbumsUseCase(pageSize = 10).cachedIn(viewModelScope)
    val artistPagingFlow = getArtistsUseCase(pageSize = 10).cachedIn(viewModelScope)
}