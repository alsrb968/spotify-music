package com.litbig.spotify.ui.home.feed.album

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.model.remote.TrackDetails
import com.litbig.spotify.core.domain.usecase.GetAlbumDetailsUseCase
import com.litbig.spotify.ui.home.feed.FeedSection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

sealed interface AlbumDetailUiState {
    data object Loading : AlbumDetailUiState
    data class Ready(
        val imageUrl: String?,
        val albumName: String,
        val artistNames: String,
        val totalTime: Long,
        val trackInfos: List<TrackInfo>?
    ) : AlbumDetailUiState
}

data class TrackInfo(
    val id: String,
    val imageUrl: String?,
    val title: String,
    val artist: String,
)

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAlbumDetailsUseCase: GetAlbumDetailsUseCase
) : ViewModel() {
    private val albumId = Uri.decode(savedStateHandle.get<String>(FeedSection.ARG_ALBUM_ID))

    val state: StateFlow<AlbumDetailUiState> = flow<AlbumDetailUiState> {
        Timber.i("albumId: $albumId")
        val albumDetails = getAlbumDetailsUseCase(albumId)
        emit(
            AlbumDetailUiState.Ready(
                imageUrl = albumDetails.images.firstOrNull()?.url,
                albumName = albumDetails.name,
                artistNames = albumDetails.artists.joinToString { it.name },
                totalTime = albumDetails.tracks?.items?.sumOf { it.durationMs }?.toLong() ?: 0L,
                trackInfos = albumDetails.tracks?.items?.map {
                    TrackInfo(
                        id = it.id,
                        imageUrl = albumDetails.images.firstOrNull()?.url,
                        title = it.name,
                        artist = it.artists.firstOrNull()?.name ?: ""
                    )
                }
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = AlbumDetailUiState.Loading
    )
}