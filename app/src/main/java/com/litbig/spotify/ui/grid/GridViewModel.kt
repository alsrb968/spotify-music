package com.litbig.spotify.ui.grid

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.core.domain.usecase.GetAlbumsUseCase
import com.litbig.spotify.core.domain.usecase.GetArtistsUseCase
import com.litbig.spotify.core.domain.usecase.favorite.GetFavoritesUseCase
import com.litbig.spotify.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class GridUiState(
    val category: String = "",
    val paging: Flow<PagingData<MusicInfo>> = flowOf(PagingData.empty()),
)

@HiltViewModel
class GridViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getFavoritesUseCase: GetFavoritesUseCase,
    getAlbumsUseCase: GetAlbumsUseCase,
    getArtistsUseCase: GetArtistsUseCase,
) : ViewModel() {
    private val category = Uri.decode(savedStateHandle.get<String>(Screen.ARG_CATEGORY))

    val state: StateFlow<GridUiState> = flow {
        when (category) {
            "favorite" -> {
                val paging = getFavoritesUseCase(pageSize = 20).cachedIn(viewModelScope)
                emit(GridUiState(category = category, paging = paging))
            }

            "album" -> {
                val paging = getAlbumsUseCase(pageSize = 20).cachedIn(viewModelScope)
                emit(GridUiState(category = category, paging = paging))
            }

            "artist" -> {
                val paging = getArtistsUseCase(pageSize = 20).cachedIn(viewModelScope)
                emit(GridUiState(category = category, paging = paging))
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = GridUiState()
    )
}