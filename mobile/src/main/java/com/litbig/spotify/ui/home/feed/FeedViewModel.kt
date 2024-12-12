package com.litbig.spotify.ui.home.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.usecase.GetAlbumDetailsListOfArtistsUseCase
import com.litbig.spotify.core.domain.usecase.GetNewAlbumReleasesUseCase
import com.litbig.spotify.core.domain.usecase.SearchArtistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class FeedItem(
    val id: String,
    val imageUrl: String?,
    val name: String,
    val type: String,
)

data class FeedCollection(
    val title: String,
    val feeds: List<FeedItem>,
)

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Ready(
        val feedCollections: List<FeedCollection>,
    ) : FeedUiState
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getNewAlbumReleasesUseCase: GetNewAlbumReleasesUseCase,
    private val getAlbumDetailsListOfArtistsUseCase: GetAlbumDetailsListOfArtistsUseCase,
    private val searchArtistUseCase: SearchArtistUseCase,
) : ViewModel() {

    private val collections = MutableStateFlow(mutableListOf<FeedCollection>())

    val state: StateFlow<FeedUiState> = combine(
        getNewAlbumReleases(),
        getArtistFeeds("NewJeans", "QWER", "IVE", "KISS OF LIFE", "ITZY"),
        getAlbumFeedsOfArtists("ROSE", "Aespa", "Madison Beer", "Sabrina Carpenter"),
    ) { newAlbumFeed, artistsFeed, albumFeeds ->
        FeedUiState.Ready(
            feedCollections = mutableListOf<FeedCollection>().apply {
                addAll(newAlbumFeed)
                addAll(artistsFeed)
                addAll(albumFeeds)
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FeedUiState.Loading
    )

    init {
        viewModelScope.launch {
            launch {
                collections.collectLatest {
                    Timber.i("size: ${it.size}")
                }
            }
        }
    }

    private fun getNewAlbumReleases(): Flow<List<FeedCollection>> {
        return flow {
            getNewAlbumReleasesUseCase()?.let { newAlbums ->
                val feeds = newAlbums.items.map { album ->
                    FeedItem(
                        id = album.id,
                        imageUrl = album.images.firstOrNull()?.url,
                        name = album.name,
                        type = "album",
                    )
                }
                val feedCollection = FeedCollection(
                    title = "New Album Releases",
                    feeds = feeds,
                )
                emit(listOf(feedCollection))
            }
        }
    }

    private fun getAlbumFeedsOfArtists(vararg artistNames: String): Flow<List<FeedCollection>> {
        return flow {
            getAlbumDetailsListOfArtistsUseCase(*artistNames).mapIndexed { index, albumDetailsList ->
                val artistName = artistNames[index]
                Timber.i("artistName: $artistName")
                val albumFeeds = albumDetailsList.map { album ->
                    FeedItem(
                        id = album.id,
                        imageUrl = album.images.firstOrNull()?.url,
                        name = album.name,
                        type = "album",
                    )
                }
                FeedCollection(
                    title = "${artistName}'s Albums",
                    feeds = albumFeeds,
                )
            }.let {
                emit(it)
            }
        }
    }

    private fun getArtistFeeds(vararg artistNames: String): Flow<List<FeedCollection>> {
        return flow {
            searchArtistUseCase(*artistNames).map { artist ->
                FeedItem(
                    id = artist.id,
                    imageUrl = artist.images?.firstOrNull()?.url,
                    name = artist.name,
                    type = "artist",
                )
            }.let { feeds ->
                emit(listOf(FeedCollection(title = "Artists", feeds = feeds)))
            }
        }
    }
}
