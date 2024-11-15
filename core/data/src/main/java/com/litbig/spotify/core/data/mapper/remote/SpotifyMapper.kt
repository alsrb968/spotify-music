package com.litbig.spotify.core.data.mapper.remote

import com.litbig.spotify.core.data.model.remote.ArtistDetailsResponse
import com.litbig.spotify.core.data.model.remote.ExternalUrlsResponse
import com.litbig.spotify.core.data.model.remote.FollowersResponse
import com.litbig.spotify.core.data.model.remote.ImageResponse
import com.litbig.spotify.core.domain.model.ArtistDetails
import com.litbig.spotify.core.domain.model.ExternalUrls
import com.litbig.spotify.core.domain.model.Followers
import com.litbig.spotify.core.domain.model.ImageInfo

object SpotifyMapper {
    @JvmStatic
    fun ExternalUrlsResponse.toExternalUrls(): ExternalUrls {
        return ExternalUrls(
            spotify = spotify
        )
    }

    @JvmStatic
    fun FollowersResponse.toFollowers(): Followers {
        return Followers(
            href = href,
            total = total
        )
    }

    @JvmStatic
    fun ImageResponse.toImageInfo(): ImageInfo {
        return ImageInfo(
            url = url,
            height = height,
            width = width
        )
    }

    @JvmStatic
    fun List<ImageResponse>.toImageInfoList(): List<ImageInfo> {
        return map { it.toImageInfo() }
    }

    @JvmStatic
    fun ArtistDetailsResponse.toArtistDetails(): ArtistDetails {
        return ArtistDetails(
            externalUrls = externalUrls.toExternalUrls(),
            followers = followers.toFollowers(),
            genres = genres,
            href = href,
            id = id,
            images = images.toImageInfoList(),
            name = name,
            popularity = popularity,
            type = type,
            uri = uri
        )
    }
}