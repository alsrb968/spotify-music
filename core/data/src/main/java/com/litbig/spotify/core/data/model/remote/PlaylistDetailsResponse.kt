package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class PlaylistDetailsResponse(
    @SerializedName("collaborative") val collaborative: Boolean,
    @SerializedName("description") val description: String,
    @SerializedName("external_urls") val externalUrls: ExternalUrlsResponse,
    @SerializedName("href") val href: String,
    @SerializedName("id") val id: String,
    @SerializedName("images") val images: List<ImageInfoResponse>,
    @SerializedName("name") val name: String,
    @SerializedName("owner") val owner: OwnerResponse,
    @SerializedName("public") val public: Boolean,
    @SerializedName("snapshot_id") val snapshotId: String,
    @SerializedName("tracks") val tracks: TracksResponse,
    @SerializedName("type") val type: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("primary_color") val primaryColor: String?,
)
