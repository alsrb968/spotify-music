@file:OptIn(ExperimentalCoroutinesApi::class)

package com.litbig.spotify.ui.home.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.repository.SpotifyRepository
import com.litbig.spotify.core.domain.usecase.spotify.GetAlbumDetailsListOfArtistsUseCase
import com.litbig.spotify.core.domain.usecase.spotify.GetNewAlbumReleasesUseCase
import com.litbig.spotify.core.domain.usecase.spotify.SearchArtistUseCase
import com.litbig.spotify.ui.models.FeedCollectionType
import com.litbig.spotify.ui.models.FeedCollectionUiModel
import com.litbig.spotify.ui.models.FeedUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Ready(
        val feedCollections: List<FeedCollectionUiModel>,
    ) : FeedUiState
}

sealed interface FeedUiIntent {
    data class SelectAlbum(val albumId: String) : FeedUiIntent
    data class SelectArtist(val artistId: String) : FeedUiIntent
    data class ShowMore(val feedCollection: FeedCollectionUiModel) : FeedUiIntent
}

sealed interface FeedUiEffect {
    data class NavigateToAlbumDetail(val albumId: String) : FeedUiEffect
    data class NavigateToArtistDetail(val artistId: String) : FeedUiEffect
    data class ShowMore(val feedCollection: FeedCollectionUiModel) : FeedUiEffect
    data class ShowToast(val message: String) : FeedUiEffect
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getNewAlbumReleasesUseCase: GetNewAlbumReleasesUseCase,
    private val getAlbumDetailsListOfArtistsUseCase: GetAlbumDetailsListOfArtistsUseCase,
    private val searchArtistUseCase: SearchArtistUseCase,
    private val spotifyRepository: SpotifyRepository,
) : ViewModel() {

    private val feedCollections =
        MutableStateFlow<List<FeedCollectionUiModel>>(mutableListOf())

    val state: StateFlow<FeedUiState> = feedCollections.flatMapLatest {
        flow {
            emit(FeedUiState.Ready(it))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FeedUiState.Loading
    )

    private val _intent = MutableSharedFlow<FeedUiIntent>(extraBufferCapacity = 1)

    private val _effect = MutableSharedFlow<FeedUiEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<FeedUiEffect> = _effect

    init {
        handleIntents()
        viewModelScope.launch {
            launch {
                getNewAlbumReleases().collectLatest {
                    feedCollections.value += it
                }
            }
            launch {
                getArtistFeeds(
                    "Bebe Rexha",
                    "NewJeans",
                    "QWER",
                    "IVE",
                    "KISS OF LIFE",
                    "BTS",
                    "BLACKPINK",
                    "TWICE",
                    "Red Velvet",
                ).collectLatest {
                    feedCollections.value += it
                }
            }
            launch {
                getAlbumFeedsOfArtists(
                    "ROSE",
                    "Aespa",
                    "Madison Beer",
                    "Ariana Grande",
                    "Dua Lipa",
                    "Olivia Rodrigo",
                    "Billie Eilish"
                ).collectLatest {
                    feedCollections.value += it
                }
            }
        }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intent.collectLatest { intent ->
                when (intent) {
                    is FeedUiIntent.SelectAlbum -> selectAlbum(intent.albumId)
                    is FeedUiIntent.SelectArtist -> selectArtist(intent.artistId)
                    is FeedUiIntent.ShowMore -> showMore(intent.feedCollection)
                }
            }
        }
    }

    fun sendIntent(intent: FeedUiIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private fun getNewAlbumReleases(): Flow<FeedCollectionUiModel> {
        return flow {
            getNewAlbumReleasesUseCase()?.let { albums ->
                albums.items.map { albumDetails ->
                    FeedUiModel.from(albumDetails)
                }
            }?.let { feeds ->
                FeedCollectionUiModel(
                    title = "새 앨범을 소개합니다!",
                    titleType = FeedCollectionType.NEW_ALBUM_RELEASES,
                    feeds = feeds,
                )
            }?.let {
                emit(it)
            }
        }
    }

    private fun getArtistFeeds(vararg artistNames: String): Flow<FeedCollectionUiModel> {
        return flow {
            searchArtistUseCase(*artistNames).map { artistDetails ->
                FeedUiModel.from(artistDetails)
            }.let { feeds ->
                FeedCollectionUiModel(
                    title = "인기 아티스트",
                    titleType = FeedCollectionType.ARTISTS,
                    feeds = feeds
                )
            }.let {
                emit(it)
            }
        }
    }

    private fun getAlbumFeedsOfArtists(vararg artistNames: String): Flow<List<FeedCollectionUiModel>> {
        return flow {
            artistNames
                .mapNotNull { spotifyRepository.searchArtists(it)?.first() }
                .map { artistDetails ->
                    val name = artistDetails.name
                    val imageUrl = artistDetails.images?.firstOrNull()?.url
                    val albumDetailsList = spotifyRepository.getAlbumsOfArtist(artistDetails.id).items
                    val feeds = albumDetailsList.map { albumDetails ->
                        FeedUiModel.from(albumDetails)
                    }
                    FeedCollectionUiModel(
                        imageUrl = imageUrl,
                        title = name,
                        titleType = FeedCollectionType.ALBUMS_OF_ARTISTS,
                        feeds = feeds,
                    )
                }.let { collections ->
                    emit(collections)
                }
        }
    }

    private fun selectAlbum(albumId: String) {
        viewModelScope.launch {
            _effect.emit(FeedUiEffect.NavigateToAlbumDetail(albumId))
        }
    }

    private fun selectArtist(artistId: String) {
        viewModelScope.launch {
            _effect.emit(FeedUiEffect.NavigateToArtistDetail(artistId))
        }
    }

    private fun showMore(feedCollection: FeedCollectionUiModel) {
        viewModelScope.launch {
            _effect.emit(FeedUiEffect.ShowMore(feedCollection))
            _effect.emit(FeedUiEffect.ShowToast("Show more not implemented"))
        }
    }
}
