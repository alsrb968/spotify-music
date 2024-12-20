package com.litbig.spotify.ui.home.album

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.design.extension.darkenColor
import com.litbig.spotify.core.domain.model.remote.AlbumDetails
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.model.remote.PlaylistDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import com.litbig.spotify.core.domain.usecase.spotify.GetAlbumDetailsUseCase
import com.litbig.spotify.core.domain.usecase.favorite.IsFavoriteUseCase
import com.litbig.spotify.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.litbig.spotify.core.domain.usecase.spotify.GetSeveralArtistDetailsUseCase
import com.litbig.spotify.ui.home.HomeSection
import com.litbig.spotify.ui.models.AlbumUiModel
import com.litbig.spotify.ui.models.ArtistUiModel
import com.litbig.spotify.ui.models.PlaylistUiModel
import com.litbig.spotify.ui.models.TrackUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface AlbumDetailUiState {
    data object Loading : AlbumDetailUiState
    data class Ready(
        val album: AlbumUiModel,
        val artists: List<ArtistUiModel>,
        val tracks: List<TrackUiModel>,
        val playlists: List<PlaylistUiModel>,
        val playingTrackId: String?,
    ) : AlbumDetailUiState
}

sealed interface AlbumDetailUiIntent {
    data class PlayTrack(val trackId: String) : AlbumDetailUiIntent
    data class PlayTracks(val trackIds: List<String>) : AlbumDetailUiIntent
    data class AddTrack(val trackId: String) : AlbumDetailUiIntent
    data class AddTracks(val trackIds: List<String>) : AlbumDetailUiIntent
    data class ToggleFavoriteAlbum(val albumId: String) : AlbumDetailUiIntent
    data class ToggleFavoriteTrack(val trackId: String) : AlbumDetailUiIntent
    data class SetDominantColor(val color: Color) : AlbumDetailUiIntent
}

sealed interface AlbumDetailUiEffect {
    data class ShowToast(val message: String) : AlbumDetailUiEffect
}

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAlbumDetailsUseCase: GetAlbumDetailsUseCase,
    private val getSeveralArtistDetailsUseCase: GetSeveralArtistDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val spotifyRepository: SpotifyRepository,
) : ViewModel() {
    private val albumId = Uri.decode(savedStateHandle.get<String>(HomeSection.ARG_ALBUM_ID))

    private val albumDetails = MutableStateFlow<AlbumDetails?>(null)
    private val artistDetailsList = MutableStateFlow<List<ArtistDetails>>(emptyList())
    private val playlistDetailsList = MutableStateFlow<List<PlaylistDetails>>(emptyList())
    private val dominantColor = MutableStateFlow(Color.Transparent)

    val state: StateFlow<AlbumDetailUiState> = combine(
        albumDetails,
        artistDetailsList,
        playlistDetailsList,
        dominantColor,
        spotifyRepository.currentMediaItem,
    ) { album, artists, playlists, color, currentItem ->
        if (album == null) {
            return@combine AlbumDetailUiState.Loading
        }

        val imageUrl = album.images.firstOrNull()?.url
        AlbumDetailUiState.Ready(
            album = AlbumUiModel.from(album).copy(dominantColor = color),
            artists = artists.map { ArtistUiModel.from(it) },
            playlists = playlists.map { PlaylistUiModel.from(it) },
            tracks = album.tracks?.items?.map {
                TrackUiModel.from(it).copy(imageUrl = imageUrl)
            } ?: emptyList(),
            playingTrackId = currentItem
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = AlbumDetailUiState.Loading
    )

    private val _intent = MutableSharedFlow<AlbumDetailUiIntent>(extraBufferCapacity = 1)

    private val _effect = MutableSharedFlow<AlbumDetailUiEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<AlbumDetailUiEffect> = _effect

    init {
        viewModelScope.launch {
            launch {
                getAlbumDetailsUseCase(albumId).collectLatest {
                    albumDetails.value = it
                }
            }
            launch {
                albumDetails.collectLatest { albumDetails ->
                    if (albumDetails == null) return@collectLatest
                    val artistIds = albumDetails.artists.map { it.id }
                    getSeveralArtistDetailsUseCase(artistIds.joinToString(",")).collectLatest { artists ->
                        artistDetailsList.value = artists
                    }
                    val albumName = albumDetails.name
                    playlistDetailsList.value = spotifyRepository.searchPlaylistOfArtist(albumName) ?: emptyList()
                }
            }
        }

        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intent.collectLatest { intent ->
                when (intent) {
                    is AlbumDetailUiIntent.PlayTrack -> playTrack(intent.trackId)
                    is AlbumDetailUiIntent.PlayTracks -> playTracks(intent.trackIds)
                    is AlbumDetailUiIntent.AddTrack -> addPlaylist(intent.trackId)
                    is AlbumDetailUiIntent.AddTracks -> addPlaylists(intent.trackIds)
                    is AlbumDetailUiIntent.ToggleFavoriteAlbum -> toggleFavoriteAlbum(intent.albumId)
                    is AlbumDetailUiIntent.ToggleFavoriteTrack -> toggleFavoriteTrack(intent.trackId)
                    is AlbumDetailUiIntent.SetDominantColor -> setDominantColor(intent.color)
                }
            }
        }
    }

    fun sendIntent(intent: AlbumDetailUiIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    fun isFavoriteTrack(trackId: String): Flow<Boolean> {
        return isFavoriteUseCase.isFavoriteTrack(trackId)
    }

    fun isFavoriteAlbum(albumId: String): Flow<Boolean> {
        return isFavoriteUseCase.isFavoriteAlbum(albumId)
    }

    private fun playTrack(trackId: String) {
        Timber.w("play trackId: $trackId")
        spotifyRepository.playTrack(trackId)
    }

    private fun playTracks(trackIdList: List<String>) {
        Timber.w("play trackIdList: $trackIdList")
        spotifyRepository.playTracks(trackIdList)
    }

    private fun addPlaylist(trackId: String) {
        spotifyRepository.addTrack(trackId)
    }

    private fun addPlaylists(trackIdList: List<String>) {
        spotifyRepository.addTracks(trackIdList)
    }

    private fun toggleFavoriteAlbum(albumId: String) {
        viewModelScope.launch {
            if (toggleFavoriteUseCase.toggleFavoriteAlbum(albumId, null)) {
                _effect.emit(AlbumDetailUiEffect.ShowToast("Added to favorite"))
            } else {
                _effect.emit(AlbumDetailUiEffect.ShowToast("Removed from favorite"))
            }
        }
    }

    private fun toggleFavoriteTrack(trackId: String) {
        viewModelScope.launch {
            if (toggleFavoriteUseCase.toggleFavoriteTrack(trackId, null)) {
                _effect.emit(AlbumDetailUiEffect.ShowToast("Added to favorite"))
            } else {
                _effect.emit(AlbumDetailUiEffect.ShowToast("Removed from favorite"))
            }
        }
    }

    private fun setDominantColor(color: Color) {
        dominantColor.value = color.darkenColor(0.5f)
    }
}