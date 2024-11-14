package com.litbig.spotify.core.data.mapper.local

import android.graphics.Bitmap
import com.litbig.spotify.core.data.model.local.MusicMetadataEntity
import com.litbig.spotify.core.domain.model.MusicMetadata
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object MusicMetadataMapper {
    @JvmStatic
    fun MusicMetadataEntity.toMusicMetadata(albumArt: Bitmap?): MusicMetadata {
        return MusicMetadata(
            absolutePath = absolutePath,
            title = title,
            artist = artist,
            album = album,
            albumArt = albumArt,
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
            numTracks = numTracks
        )
    }

    @JvmStatic
    fun List<MusicMetadataEntity>.toMusicMetadataList(albumArts: List<Bitmap?>): List<MusicMetadata> {
        return mapIndexed { index, musicMetadataEntity ->
            musicMetadataEntity.toMusicMetadata(albumArts[index])
        }
    }

    @JvmStatic
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
            numTracks = numTracks
        )
    }

    @JvmStatic
    fun List<MusicMetadata>.toMusicMetadataEntityList(): List<MusicMetadataEntity> {
        return map { it.toMusicMetadataEntity() }
    }

    @JvmStatic
    fun Long.toDuration(): Duration {
        return this.milliseconds
    }

    @JvmStatic
    fun Duration.toLong(): Long {
        return this.inWholeMilliseconds
    }
}