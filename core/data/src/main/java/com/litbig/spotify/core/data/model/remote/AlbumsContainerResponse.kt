package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class AlbumsContainerResponse(
    @SerializedName("albums") val albums: List<AlbumDetailsResponse>
)
