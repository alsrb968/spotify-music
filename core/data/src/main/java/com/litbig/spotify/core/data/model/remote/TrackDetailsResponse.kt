package com.litbig.spotify.core.data.model.remote

import com.google.gson.annotations.SerializedName

data class TrackDetailsResponse(
    @SerializedName("album") val album: AlbumDetailsResponse,
    @SerializedName("artists") val artists: List<ArtistDetailsResponse>,
    @SerializedName("available_markets") val availableMarkets: List<String>?,
    @SerializedName("disc_number") val discNumber: Int,
    @SerializedName("duration_ms") val durationMs: Int,
    @SerializedName("explicit") val explicit: Boolean,
    @SerializedName("external_ids") val externalIds: ExternalIdsResponse,
    @SerializedName("external_urls") val externalUrls: ExternalUrlsResponse,
    @SerializedName("href") val href: String,
    @SerializedName("id") val id: String,
    @SerializedName("is_playable") val isPlayable: Boolean,
    @SerializedName("linked_from") val linkedFrom: LinkedFromResponse?,
    @SerializedName("name") val name: String,
    @SerializedName("popularity") val popularity: Int,
    @SerializedName("preview_url") val previewUrl: String?,
    @SerializedName("track_number") val trackNumber: Int,
    @SerializedName("type") val type: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("is_local") val isLocal: Boolean
)
