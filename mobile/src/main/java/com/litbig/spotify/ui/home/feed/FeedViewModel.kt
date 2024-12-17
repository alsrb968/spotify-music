@file:OptIn(ExperimentalCoroutinesApi::class)

package com.litbig.spotify.ui.home.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.usecase.GetAlbumDetailsListOfArtistsUseCase
import com.litbig.spotify.core.domain.usecase.GetNewAlbumReleasesUseCase
import com.litbig.spotify.core.domain.usecase.SearchArtistUseCase
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

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getNewAlbumReleasesUseCase: GetNewAlbumReleasesUseCase,
    private val getAlbumDetailsListOfArtistsUseCase: GetAlbumDetailsListOfArtistsUseCase,
    private val searchArtistUseCase: SearchArtistUseCase,
) : ViewModel() {

    private val feedCollections =
        MutableStateFlow<List<FeedCollectionUiModel>>(mutableListOf())

    init {
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

    val state: StateFlow<FeedUiState> = feedCollections.flatMapLatest {
        flow {
            emit(FeedUiState.Ready(it))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FeedUiState.Loading
    )

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
}
