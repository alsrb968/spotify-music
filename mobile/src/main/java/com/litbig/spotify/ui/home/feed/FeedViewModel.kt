@file:OptIn(ExperimentalCoroutinesApi::class)

package com.litbig.spotify.ui.home.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.usecase.spotify.GetAlbumDetailsListOfArtistsUseCase
import com.litbig.spotify.core.domain.usecase.spotify.GetNewAlbumReleasesUseCase
import com.litbig.spotify.core.domain.usecase.spotify.SearchArtistUseCase
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
                    "NewJeans",
                    "QWER",
                    "IVE",
                    "KISS OF LIFE",
                    "BTS",
                    "BLACKPINK",
                    "TWICE",
                    "IU",
                    "Red Velvet",
                    "MAMAMOO"
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
                    title = "New Album Releases",
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
                    title = "Artists",
                    feeds = feeds
                )
            }.let {
                emit(it)
            }
        }
    }

    private fun getAlbumFeedsOfArtists(vararg artistNames: String): Flow<List<FeedCollectionUiModel>> {
        return flow {
            getAlbumDetailsListOfArtistsUseCase(*artistNames).map { albumDetailsList ->
                val name = albumDetailsList.firstOrNull()?.artists?.firstOrNull()?.name ?: ""
                val feeds = albumDetailsList.map { albumDetails ->
                    FeedUiModel.from(albumDetails)
                }
                Pair(name, feeds)
            }.map { pair ->
                val (name, feeds) = pair
                FeedCollectionUiModel(
                    title = "${name}'s Albums",
                    feeds = feeds,
                )
            }.let {
                emit(it)
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
