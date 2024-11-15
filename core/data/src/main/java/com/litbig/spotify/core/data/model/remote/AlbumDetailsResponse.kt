package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class AlbumDetailsResponse(
    @SerializedName("album_type") val albumType: String,
    @SerializedName("total_tracks") val totalTracks: Int,
    @SerializedName("available_markets") val availableMarkets: List<String>?,
    @SerializedName("external_urls") val externalUrls: ExternalUrlsResponse,
    @SerializedName("href") val href: String,
    @SerializedName("id") val id: String,
    @SerializedName("images") val images: List<ImageInfoResponse>,
    @SerializedName("name") val name: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("release_date_precision") val releaseDatePrecision: String,
    @SerializedName("type") val type: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("artists") val artists: List<ArtistDetailsResponse>,
    @SerializedName("tracks") val tracks: TracksResponse?,
    @SerializedName("copyrights") val copyrights: List<CopyrightResponse>?,
    @SerializedName("external_ids") val externalIds: ExternalIdsResponse?,
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("label") val label: String?,
    @SerializedName("popularity") val popularity: Int?,
    @SerializedName("is_playable") val isPlayable: Boolean
)
