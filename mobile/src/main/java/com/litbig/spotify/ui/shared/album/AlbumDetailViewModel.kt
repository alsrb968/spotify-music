package com.litbig.spotify.ui.shared.album

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.design.extension.darkenColor
import com.litbig.spotify.core.domain.extension.combine
import com.litbig.spotify.core.domain.model.remote.AlbumDetails
import com.litbig.spotify.core.domain.model.remote.ArtistDetails
import com.litbig.spotify.core.domain.model.remote.PlaylistDetails
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import com.litbig.spotify.core.domain.usecase.spotify.GetAlbumDetailsUseCase
import com.litbig.spotify.core.domain.usecase.favorite.IsFavoriteUseCase
import com.litbig.spotify.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.litbig.spotify.core.domain.usecase.spotify.GetSeveralArtistDetailsUseCase
import com.litbig.spotify.ui.models.AlbumUiModel
import com.litbig.spotify.ui.models.ArtistUiModel
import com.litbig.spotify.ui.models.PlaylistUiModel
import com.litbig.spotify.ui.models.TrackUiModel
import com.litbig.spotify.ui.shared.DetailsSection
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
        val isFavorite: Boolean,
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
    data class NavigateToAlbumDetail(val albumId: String) : AlbumDetailUiIntent
    data class NavigateToArtistDetail(val artistId: String) : AlbumDetailUiIntent
    data class NavigateToPlaylistDetail(val playlistId: String) : AlbumDetailUiIntent
    data object NavigateBack : AlbumDetailUiIntent
    data class SetDominantColor(val color: Color) : AlbumDetailUiIntent
}

sealed interface AlbumDetailUiEffect {
    data class NavigateToAlbumDetail(val albumId: String) : AlbumDetailUiEffect
    data class NavigateToArtistDetail(val artistId: String) : AlbumDetailUiEffect
    data class NavigateToPlaylistDetail(val playlistId: String) : AlbumDetailUiEffect
    data object NavigateBack : AlbumDetailUiEffect
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
    private val albumId = Uri.decode(savedStateHandle.get<String>(DetailsSection.ARG_ALBUM_ID))

    private val albumDetails = MutableStateFlow<AlbumDetails?>(null)
    private val artistDetailsList = MutableStateFlow<List<ArtistDetails>>(emptyList())
    private val playlistDetailsList = MutableStateFlow<List<PlaylistDetails>>(emptyList())
    private val isFavorite = MutableStateFlow(false)
    private val dominantColor = MutableStateFlow(Color.Transparent)

    val state: StateFlow<AlbumDetailUiState> = combine(
        albumDetails,
        artistDetailsList,
        playlistDetailsList,
        isFavorite,
        dominantColor,
        spotifyRepository.currentMediaItem,
    ) { album, artists, playlists, isFav, color, currentItem ->
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
            isFavorite = isFav,
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
            launch {
                isFavoriteUseCase.isFavoriteAlbum(albumId).collectLatest { isFav ->
                    isFavorite.value = isFav
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
                    is AlbumDetailUiIntent.NavigateToAlbumDetail -> navigateToAlbumDetail(intent.albumId)
                    is AlbumDetailUiIntent.NavigateToArtistDetail -> navigateToArtistDetail(intent.artistId)
                    is AlbumDetailUiIntent.NavigateToPlaylistDetail -> navigateToPlaylistDetail(intent.playlistId)
                    is AlbumDetailUiIntent.NavigateBack -> navigateBack()
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

    private fun navigateToAlbumDetail(albumId: String) {
        _effect.tryEmit(AlbumDetailUiEffect.NavigateToAlbumDetail(albumId))
    }

    private fun navigateToArtistDetail(artistId: String) {
        _effect.tryEmit(AlbumDetailUiEffect.NavigateToArtistDetail(artistId))
    }

    private fun navigateToPlaylistDetail(playlistId: String) {
        _effect.tryEmit(AlbumDetailUiEffect.NavigateToPlaylistDetail(playlistId))
    }

    private fun navigateBack() {
        _effect.tryEmit(AlbumDetailUiEffect.NavigateBack)
    }

    private fun setDominantColor(color: Color) {
        dominantColor.value = color.darkenColor(0.5f)
    }
}