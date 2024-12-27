package com.litbig.spotify.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.flatMap
import androidx.paging.map
import com.litbig.spotify.core.domain.model.local.Favorite
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import com.litbig.spotify.ui.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data class Ready(
        val uiModels: List<UiModel>,
    ) : LibraryUiState
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    spotifyRepository: SpotifyRepository,
) : ViewModel() {

    private val allUiModels = MutableStateFlow<List<UiModel>>(emptyList())


    val state: StateFlow<LibraryUiState> = allUiModels.map {
        if (it.isEmpty()) return@map LibraryUiState.Loading

        LibraryUiState.Ready(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LibraryUiState.Loading
    )

    init {
        viewModelScope.launch {
            launch {
                spotifyRepository.getFavorites().collectLatest {
                    allUiModels.value = it.map { fav ->
                        when (fav.type) {
                            "track" -> {
                                TrackUiModel.from(spotifyRepository.getTrackDetails(fav.name))
                            }

                            "album" -> {
                                AlbumUiModel.from(spotifyRepository.getAlbumDetails(fav.name))
                            }

                            "artist" -> {
                                ArtistUiModel.from(spotifyRepository.getArtistDetails(fav.name))
                            }

                            "playlist" -> {
                                PlaylistUiModel.from(spotifyRepository.getPlaylistDetails(fav.name))
                            }

                            else -> return@collectLatest
                        }
                    }
                }
            }
        }
    }
}