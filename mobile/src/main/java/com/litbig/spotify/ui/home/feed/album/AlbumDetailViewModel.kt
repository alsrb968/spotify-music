package com.litbig.spotify.ui.home.feed.album

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.data.di.RepositoryModule.MockingPlayerRepository
import com.litbig.spotify.core.design.extension.darkenColor
import com.litbig.spotify.core.domain.repository.PlayerRepository
import com.litbig.spotify.core.domain.usecase.GetAlbumDetailsUseCase
import com.litbig.spotify.core.domain.usecase.favorite.IsFavoriteUseCase
import com.litbig.spotify.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.litbig.spotify.ui.home.feed.FeedSection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface AlbumDetailUiState {
    data object Loading : AlbumDetailUiState
    data class Ready(
        val imageUrl: String?,
        val albumName: String,
        val artistNames: String,
        val totalTime: Long,
        val trackInfos: List<TrackInfo>?,
        val dominantColor: Color
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
    private val getAlbumDetailsUseCase: GetAlbumDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    @MockingPlayerRepository private val playerRepository: PlayerRepository
) : ViewModel() {
    private val albumId = Uri.decode(savedStateHandle.get<String>(FeedSection.ARG_ALBUM_ID))

    private val dominantColor = MutableStateFlow(Color.Transparent)

    val state: StateFlow<AlbumDetailUiState> = combine(
        getAlbumDetailsUseCase(albumId),
        dominantColor
    ) { albumDetails, color ->
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
                    artist = it.artists.firstOrNull()?.name ?: "",
                )
            },
            dominantColor = color
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = AlbumDetailUiState.Loading
    )

    fun isFavoriteTrack(trackName: String): Flow<Boolean> {
        return isFavoriteUseCase.isFavoriteTrack(trackName)
    }

    fun isFavoriteAlbum(albumName: String): Flow<Boolean> {
        return isFavoriteUseCase.isFavoriteAlbum(albumName)
    }

    fun toggleFavoriteAlbum(albumName: String, imageUrl: String? = null) {
        viewModelScope.launch {
            toggleFavoriteUseCase.toggleFavoriteAlbum(albumName, imageUrl)
        }
    }

    fun play(trackId: String) {
        Timber.w("play trackId: $trackId")
        playerRepository.play(trackId)
    }

    fun play(trackIdList: List<String>) {
        Timber.w("play trackIdList: $trackIdList")
        playerRepository.play(trackIdList)
    }

    fun addPlaylist(trackIdList: List<String>) {
        playerRepository.addPlayLists(trackIdList)
    }

    fun setDominantColor(color: Color) {
        dominantColor.value = color.darkenColor(0.5f)
    }
}