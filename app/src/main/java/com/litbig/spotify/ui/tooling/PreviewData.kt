package com.litbig.spotify.ui.tooling

import androidx.paging.PagingData
import com.litbig.spotify.core.data.mapper.local.MusicMetadataMapper.toDuration
import com.litbig.spotify.core.domain.model.Album
import com.litbig.spotify.core.domain.model.MusicMetadata
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

val PreviewMusicMetadata = MusicMetadata(
    absolutePath = "",
    title = "Ocean Eyes",
    artist = "Billie Eilish",
    album = "Ocean Eyes",
    genre = "Pop",
    albumArt = null,
    duration = 262000L.toDuration(),
    year = "2015",
    albumArtist = "Billie Eilish",
    composer = "Billie Eilish",
    writer = "Billie Eilish",
    cdTrackNumber = "1",
    discNumber = "1",
    date = "2015-11-18",
    mimeType = "audio/mpeg",
    compilation = "false",
    hasAudio = true,
    bitrate = "320000",
    numTracks = "1",
)

val PreviewMusicMetadataList = listOf(
    PreviewMusicMetadata,
    PreviewMusicMetadata,
    PreviewMusicMetadata,
)

val PreviewMusicMetadataPagingData = flow<PagingData<MusicMetadata>> {
    flowOf(PagingData.from(PreviewMusicMetadataList))
}

val PreviewAlbum = Album(
    name = "Ocean Eyes",
    artist = "Billie Eilish",
    albumArt = null,
    musicCount = 3,
)

val PreviewAlbumPagingData = flow<PagingData<Album>> {
    flowOf(
        PagingData.from(
            listOf(
                PreviewAlbum,
                PreviewAlbum,
                PreviewAlbum,
            )
        )
    )
}