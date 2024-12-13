package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class ArtistsContainerResponse(
    @SerializedName("artists") val artists: List<ArtistDetailsResponse>
)
