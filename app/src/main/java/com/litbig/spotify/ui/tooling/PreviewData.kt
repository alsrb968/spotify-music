package com.litbig.spotify.ui.tooling

import androidx.paging.PagingData
import com.litbig.spotify.core.data.mapper.local.toDuration
import com.litbig.spotify.core.domain.model.MusicInfo
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

val PreviewMusicMetadata = MusicMetadata(
    absolutePath = "",
    title = "Ocean Eyes",
    artist = "Billie Eilish",
    album = "Ocean Eyes",
    genre = "Pop",
    albumArtUrl = null,
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

val PreviewMusicMetadataList = List(20) { PreviewMusicMetadata }

val PreviewMusicMetadataPagingData = flow<PagingData<MusicMetadata>> {
    flowOf(PagingData.from(PreviewMusicMetadataList))
}

val PreviewMusicInfo = MusicInfo(
    imageUrl = "https://example.com/image.jpg",
    title = "Ocean Eyes",
    content = "Billie Eilish",
    category = "album",
)

val PreviewMusicInfoList = List(20) { PreviewMusicInfo }

val PreviewMusicInfoListFlow = flowOf(PreviewMusicInfoList)

val PreviewMusicInfoPagingData = flow<PagingData<MusicInfo>> {
    flowOf(PagingData.from(List(20) { PreviewMusicInfo }))
}