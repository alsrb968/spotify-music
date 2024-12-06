package com.litbig.spotify.ui.home.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.usecase.GetArtistRelatedInfoUseCase
import com.litbig.spotify.core.domain.usecase.GetNewAlbumReleasesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
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

    private val collections = mutableListOf<FeedCollection>()

    private val _state = MutableStateFlow(FeedUiState(emptyList()))
    val state: StateFlow<FeedUiState> = _state

    init {
        viewModelScope.launch {
            launch {
                getNewAlbumReleasesUseCase().collectLatest { newAlbums ->
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
                    collections.add(feedCollection)

                    _state.value = FeedUiState(collections)
                }
            }
            launch {
                val artistName = "Sabrina Carpenter"
                getArtistRelatedInfoUseCase(artistName).collectLatest {
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
                    collections.add(albumFeedCollection)

                    val trackFeeds = topTracks.map { track ->
                        FeedAlbum(
                            id = track.id,
                            imageUrl = track.album.images.firstOrNull()?.url,
                            name = track.name,
                        )
                    }
                    val trackFeedCollection = FeedCollection(
                        title = "${artistName}'s Top Tracks",
                        feeds = trackFeeds,
                    )
                    collections.add(trackFeedCollection)

                    _state.value = FeedUiState(collections)
                }
            }
        }
    }
}