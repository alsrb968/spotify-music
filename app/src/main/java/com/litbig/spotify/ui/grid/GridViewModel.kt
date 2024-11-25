package com.litbig.spotify.ui.grid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.core.domain.usecase.GetAlbumsUseCase
import com.litbig.spotify.core.domain.usecase.GetArtistsUseCase
import com.litbig.spotify.core.domain.usecase.GetMetadataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class GridViewModel @Inject constructor(
    getMetadataUseCase: GetMetadataUseCase,
    getAlbumsUseCase: GetAlbumsUseCase,
    getArtistsUseCase: GetArtistsUseCase,
) : ViewModel() {
    val metadataPagingFlow = getMetadataUseCase(pageSize = 10)
        .cachedIn(viewModelScope)

    val albumsPagingFlow = getAlbumsUseCase(pageSize = 10).map { pagingData ->
        pagingData.map { album ->
            MusicInfo(
                imageUrl = album.imageUrl,
                title = album.name,
                content = "${album.artist} • ${album.musicCount} songs",
                category = "album"
            )
        }
    }.cachedIn(viewModelScope)

    val artistPagingFlow = getArtistsUseCase(pageSize = 10).map { pagingData ->
        pagingData.map { artist ->
            MusicInfo(
                imageUrl = artist.imageUrl,
                title = artist.name,
                content = "${artist.albumCount} albums • ${artist.musicCount} songs",
                category = "artist"
            )
        }
    }.cachedIn(viewModelScope)
}