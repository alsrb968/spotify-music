package com.litbig.spotify.ui.home.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.usecase.GetArtistRelatedInfoUseCase
import com.litbig.spotify.core.domain.usecase.GetNewAlbumReleasesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class FeedAlbum(
    val id: String,
    val imageUrl: String?,
    val name: String,
)

data class FeedCollection(
    val title: String,
    val feeds: List<FeedAlbum>,
)

data class FeedUiState(
    val feedCollections: List<FeedCollection>
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getNewAlbumReleasesUseCase: GetNewAlbumReleasesUseCase,
    private val getArtistRelatedInfoUseCase: GetArtistRelatedInfoUseCase,
) : ViewModel() {

    private val collections = MutableStateFlow(mutableListOf<FeedCollection>())

    val state: StateFlow<FeedUiState> = combine(
        getNewAlbumReleases(),
        getArtistRelatedInfos("Aespa", "Madison Beer", "Sabrina Carpenter"),
    ) { newAlbums, artistRelatedInfos ->
        FeedUiState(
            feedCollections = mutableListOf<FeedCollection>().apply {
                addAll(newAlbums)
                addAll(artistRelatedInfos)
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FeedUiState(emptyList())
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
                    FeedAlbum(
                        id = album.id,
                        imageUrl = album.images.firstOrNull()?.url,
                        name = album.name,
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

    private fun getArtistRelatedInfos(vararg artistNames: String): Flow<List<FeedCollection>> {
        return flow {
            val list = mutableListOf<FeedCollection>()
            artistNames.forEach { artistName ->
                getArtistRelatedInfoUseCase(artistName)?.let {
                    Timber.i("artistName: $artistName")
                    val (albums, topTracks) = it
                    val albumFeeds = albums.map { album ->
                        FeedAlbum(
                            id = album.id,
                            imageUrl = album.images.firstOrNull()?.url,
                            name = album.name,
                        )
                    }
                    val albumFeedCollection = FeedCollection(
                        title = "${artistName}'s Albums",
                        feeds = albumFeeds,
                    )


                    val trackFeeds = topTracks.map { track ->
                        FeedAlbum(
                            id = track.id,
                            imageUrl = track.album?.images?.firstOrNull()?.url,
                            name = track.name,
                        )
                    }
                    val trackFeedCollection = FeedCollection(
                        title = "${artistName}'s Top Tracks",
                        feeds = trackFeeds,
                    )

                    list.addAll(
                        listOf(
                            albumFeedCollection,
                            trackFeedCollection
                        )
                    )
                }
            }
            emit(list)
        }
    }
}
