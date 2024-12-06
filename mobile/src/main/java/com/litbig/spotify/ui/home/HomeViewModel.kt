package com.litbig.spotify.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.model.remote.Albums
import com.litbig.spotify.core.domain.usecase.GetNewAlbumReleasesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val newAlbumReleases: CategoryUiState = CategoryUiState.Loading
)

sealed interface CategoryUiState {
    data object Loading : CategoryUiState
    data class Ready(val albums: Albums) : CategoryUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNewAlbumReleasesUseCase: GetNewAlbumReleasesUseCase
) : ViewModel() {

}