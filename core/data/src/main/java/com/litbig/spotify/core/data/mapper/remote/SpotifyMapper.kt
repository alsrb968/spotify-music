package com.litbig.spotify.core.data.mapper.remote

import com.litbig.spotify.core.data.model.remote.*
import com.litbig.spotify.core.domain.model.remote.*

fun SearchResponse.toSearch(): Search {
    return Search(
        tracks = tracks?.toTracks(),
        artists = artists?.toArtists(),
        albums = albums?.toAlbums(),
        playlists = playlists?.toPlaylists(),
    )
}

fun TracksResponse.toTracks(): Tracks {
    return Tracks(
        href = href,
        limit = limit,
        next = next,
        offset = offset,
        previous = previous,
        total = total,
        items = items?.toTrackDetails()
    )
}

fun ArtistsResponse.toArtists(): Artists {
    return Artists(
        href = href,
        limit = limit,
        next = next,
        offset = offset,
        previous = previous,
        total = total,
        items = items.toArtistDetails()
    )
}

fun AlbumsResponse.toAlbums(): Albums {
    return Albums(
        href = href,
        limit = limit,
        next = next,
        offset = offset,
        previous = previous,
        total = total,
        items = items.toAlbumDetails()
    )
}

fun PlaylistsResponse.toPlaylists(): Playlists {
    return Playlists(
        href = href,
        limit = limit,
        next = next,
        offset = offset,
        previous = previous,
        total = total,
        items = items.mapNotNull { it?.toPlaylistDetails() }
    )
}

fun TrackDetailsResponse.toTrackDetails(): TrackDetails {
    return TrackDetails(
        album = album?.toAlbumDetails(),
        artists = artists.toArtistDetails(),
        availableMarkets = availableMarkets,
        discNumber = discNumber,
        durationMs = durationMs,
        explicit = explicit,
        externalIds = externalIds?.toExternalIds(),
        externalUrls = externalUrls.toExternalUrls(),
        href = href,
        id = id,
        isLocal = isLocal,
        isPlayable = isPlayable,
        linkedFrom = linkedFrom?.toLinkedFrom(),
        name = name,
        popularity = popularity,
        previewUrl = previewUrl,
        trackNumber = trackNumber,
        type = type,
        uri = uri
    )
}

fun List<TrackDetailsResponse>.toTrackDetails(): List<TrackDetails> {
    return map { it.toTrackDetails() }
}

fun ArtistDetailsResponse.toArtistDetails(): ArtistDetails {
    return ArtistDetails(
        externalUrls = externalUrls.toExternalUrls(),
        followers = followers?.toFollowers(),
        genres = genres,
        href = href,
        id = id,
        images = images?.toImageInfoList(),
        name = name,
        popularity = popularity,
        type = type,
        uri = uri
    )
}

fun List<ArtistDetailsResponse>.toArtistDetails(): List<ArtistDetails> {
    return map { it.toArtistDetails() }
}

fun AlbumDetailsResponse.toAlbumDetails(): AlbumDetails {
    return AlbumDetails(
        albumType = albumType,
        totalTracks = totalTracks,
        externalUrls = externalUrls.toExternalUrls(),
        href = href,
        id = id,
        images = images.toImageInfoList(),
        name = name,
        releaseDate = releaseDate,
        releaseDatePrecision = releaseDatePrecision,
        type = type,
        uri = uri,
        artists = artists.toArtistDetails(),
        tracks = tracks?.toTracks(),
        copyrights = copyrights?.toCopyrights(),
        externalIds = externalIds?.toExternalIds(),
        genres = genres,
        label = label,
        popularity = popularity,
        isPlayable = isPlayable
    )
}

fun List<AlbumDetailsResponse>.toAlbumDetails(): List<AlbumDetails> {
    return map { it.toAlbumDetails() }
}

fun PlaylistDetailsResponse.toPlaylistDetails(): PlaylistDetails {
    return PlaylistDetails(
        collaborative = collaborative,
        description = description,
        externalUrls = externalUrls.toExternalUrls(),
        href = href,
        id = id,
        images = images.toImageInfoList(),
        name = name,
        owner = owner.toOwner(),
        public = public,
        snapshotId = snapshotId,
        tracks = tracks.toTracks(),
        type = type,
        uri = uri,
        primaryColor = primaryColor
    )
}

fun List<PlaylistDetailsResponse?>.toPlaylistDetails(): List<PlaylistDetails> {
    return mapNotNull { it?.toPlaylistDetails() }
}

fun ExternalIdsResponse.toExternalIds(): ExternalIds {
    return ExternalIds(
        isrc = isrc,
        ean = ean,
        upc = upc
    )
}

fun ExternalUrlsResponse.toExternalUrls(): ExternalUrls {
    return ExternalUrls(
        spotify = spotify
    )
}

fun LinkedFromResponse.toLinkedFrom(): LinkedFrom {
    return LinkedFrom(
        externalUrls = externalUrls?.toExternalUrls(),
        href = href,
        id = id,
        type = type,
        uri = uri
    )
}

fun CopyrightResponse.toCopyrights(): Copyrights {
    return Copyrights(
        text = text,
        type = type
    )
}

fun List<CopyrightResponse>.toCopyrights(): List<Copyrights> {
    return map { it.toCopyrights() }
}

fun FollowersResponse.toFollowers(): Followers {
    return Followers(
        href = href,
        total = total
    )
}

fun ImageInfoResponse.toImageInfo(): ImageInfo {
    return ImageInfo(
        url = url,
        height = height,
        width = width
    )
}

fun List<ImageInfoResponse>.toImageInfoList(): List<ImageInfo> {
    return map { it.toImageInfo() }
}

fun OwnerResponse.toOwner(): Owner {
    return Owner(
        externalUrls = externalUrls.toExternalUrls(),
        href = href,
        id = id,
        type = type,
        uri = uri,
        displayName = displayName
    )
}