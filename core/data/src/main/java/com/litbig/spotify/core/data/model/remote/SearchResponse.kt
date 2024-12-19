package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("tracks") val tracks: TracksResponse?,
    @SerializedName("artists") val artists: ArtistsResponse?,
    @SerializedName("albums") val albums: AlbumsResponse?,
    @SerializedName("playlists") val playlists: PlaylistsResponse?,
)
