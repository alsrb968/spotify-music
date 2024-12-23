package com.litbig.spotify.core.data.mapper.local

import com.litbig.spotify.core.data.model.local.MusicMetadataEntity
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun MusicMetadataEntity.toMusicMetadata(albumArtUrl: String?): MusicMetadata {
    return MusicMetadata(
        absolutePath = absolutePath,
        title = title,
        artist = artist,
        album = album,
        albumArtUrl = albumArtUrl,
        genre = genre,
        duration = duration.toDuration(),
        year = year,
        albumArtist = albumArtist,
        composer = composer,
        writer = writer,
        cdTrackNumber = cdTrackNumber,
        discNumber = discNumber,
        date = date,
        mimeType = mimeType,
        compilation = compilation,
        hasAudio = hasAudio,
        bitrate = bitrate,
        numTracks = numTracks,
    )
}

fun MusicMetadata.toMusicMetadataEntity(): MusicMetadataEntity {
    return MusicMetadataEntity(
        absolutePath = absolutePath,
        title = title,
        artist = artist,
        album = album,
        genre = genre,
        duration = duration.toLong(),
        year = year,
        albumArtist = albumArtist,
        composer = composer,
        writer = writer,
        cdTrackNumber = cdTrackNumber,
        discNumber = discNumber,
        date = date,
        mimeType = mimeType,
        compilation = compilation,
        hasAudio = hasAudio,
        bitrate = bitrate,
        numTracks = numTracks,
    )
}

fun List<MusicMetadata>.toMusicMetadataEntityList(): List<MusicMetadataEntity> {
    return map { it.toMusicMetadataEntity() }
}

fun Long.toDuration(): Duration {
    return this.milliseconds
}

fun Duration.toLong(): Long {
    return this.inWholeMilliseconds
}