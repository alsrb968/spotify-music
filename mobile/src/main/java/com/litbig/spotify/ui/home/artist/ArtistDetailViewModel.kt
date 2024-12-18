package com.litbig.spotify.ui.home.artist

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.litbig.spotify.core.domain.usecase.spotify.GetAlbumDetailsListOfArtistsUseCase
import com.litbig.spotify.core.domain.usecase.spotify.GetArtistDetailsUseCase
import com.litbig.spotify.core.domain.usecase.spotify.GetTopTrackDetailsListOfArtistsUseCase
import com.litbig.spotify.ui.home.HomeSection
import com.litbig.spotify.ui.models.AlbumUiModel
import com.litbig.spotify.ui.models.ArtistUiModel
import com.litbig.spotify.ui.models.TrackUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

sealed interface ArtistDetailUiState {
    data object Loading : ArtistDetailUiState
    data class Ready(
        val artist: ArtistUiModel,
        val albums: List<AlbumUiModel>?,
        val topTracks: List<TrackUiModel>?,
    ) : ArtistDetailUiState
}

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getArtistDetailsUseCase: GetArtistDetailsUseCase,
    private val getAlbumDetailsListOfArtistsUseCase: GetAlbumDetailsListOfArtistsUseCase,
    private val getTopTrackDetailsListOfArtistsUseCase: GetTopTrackDetailsListOfArtistsUseCase,
) : ViewModel() {
    private val artistId = Uri.decode(savedStateHandle.get<String>(HomeSection.ARG_ARTIST_ID))

    private val dominantColor = MutableStateFlow(Color.Transparent)

}