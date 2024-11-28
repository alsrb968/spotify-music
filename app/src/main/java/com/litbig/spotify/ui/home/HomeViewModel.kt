package com.litbig.spotify.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.core.domain.usecase.GetAlbumsUseCase
import com.litbig.spotify.core.domain.usecase.GetArtistsUseCase
import com.litbig.spotify.core.domain.usecase.favorite.GetFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val favoriteState: CategoryUiState = CategoryUiState.Loading,
    val albumState: CategoryUiState = CategoryUiState.Loading,
    val artistState: CategoryUiState = CategoryUiState.Loading,
)

sealed interface CategoryUiState {
    data object Loading : CategoryUiState
    data class Ready(
        val category: String,
        val list: List<MusicInfo>
    ) : CategoryUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    getFavoritesUseCase: GetFavoritesUseCase,
    getAlbumsUseCase: GetAlbumsUseCase,
    getArtistsUseCase: GetArtistsUseCase,
) : ViewModel() {
    private val favoritesFlow = getFavoritesUseCase.getFavorites(count = 8)
    private val albumsFlow = getAlbumsUseCase.getAlbums(count = 8)
    private val artistsFlow = getArtistsUseCase.getArtists(count = 8)

    val state: StateFlow<HomeUiState> = combine(
        favoritesFlow,
        albumsFlow,
        artistsFlow,
    ) { favorites, albums, artists ->

        HomeUiState(
            favoriteState = CategoryUiState.Ready("favorite", favorites),
            albumState = CategoryUiState.Ready("album", albums),
            artistState = CategoryUiState.Ready("artist", artists),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HomeUiState()
    )

    fun onMore() {}
}