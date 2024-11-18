package com.litbig.spotify.core.data.datasource.local

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import com.litbig.spotify.core.data.mapper.local.toDuration
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.io.File
import javax.inject.Inject

interface MediaRetrieverDataSource {
    fun getAlbumArt(file: File): Bitmap?
    suspend fun getAlbumArtFlow(file: File): Flow<Bitmap?>
    fun getMusicMetadata(file: File): MusicMetadata?
    suspend fun getMusicMetadataFlow(file: File): Flow<MusicMetadata?>
}

class MediaRetrieverDataSourceImpl @Inject constructor() : MediaRetrieverDataSource {
    override fun getAlbumArt(file: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(file.absolutePath)
            val albumArt = retriever.embeddedPicture?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            }
            albumArt
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }

    override suspend fun getAlbumArtFlow(file: File): Flow<Bitmap?> {
        return flow {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(file.absolutePath)
                val albumArt = retriever.embeddedPicture?.let {
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                }
                emit(albumArt)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(null) // 실패 시 null 설정
            } finally {
                retriever.release() // 리소스 해제
            }
        }.flowOn(Dispatchers.IO) // I/O 작업을 비동기적으로 처리
    }

    override fun getMusicMetadata(file: File): MusicMetadata? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(file.absolutePath)
            val duration =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull() ?: 0L
            val metadata = MusicMetadata(
                absolutePath = file.absolutePath,
                title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    ?: file.nameWithoutExtension,
                artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                    ?: "",
                album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                    ?: "",
                genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
                    ?: "",
                albumArt = retriever.embeddedPicture?.let {
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                },
                duration = duration.toDuration(),
                year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)
                    ?: "",
                albumArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
                    ?: "",
                composer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER)
                    ?: "",
                writer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER)
                    ?: "",
                cdTrackNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
                    ?: "",
                discNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER)
                    ?: "",
                date = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
                    ?: "",
                mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                    ?: "",
                compilation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION)
                    ?: "",
                hasAudio = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)
                    ?.toBoolean() ?: false,
                bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                    ?: "",
                numTracks = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)
                    ?: "",
            )
            metadata
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }

    override suspend fun getMusicMetadataFlow(file: File): Flow<MusicMetadata?> {
        Timber.i("getMusicMetadata: ${file.absolutePath}")
        return flow {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(file.absolutePath)
                val duration =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLongOrNull() ?: 0L
                val metadata = MusicMetadata(
                    absolutePath = file.absolutePath,
                    title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                        ?: file.nameWithoutExtension,
                    artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                        ?: "",
                    album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                        ?: "",
                    genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
                        ?: "",
                    albumArt = retriever.embeddedPicture?.let {
                        BitmapFactory.decodeByteArray(it, 0, it.size)
                    },
                    duration = duration.toDuration(),
                    year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)
                        ?: "",
                    albumArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
                        ?: "",
                    composer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER)
                        ?: "",
                    writer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER)
                        ?: "",
                    cdTrackNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
                        ?: "",
                    discNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER)
                        ?: "",
                    date = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
                        ?: "",
                    mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                        ?: "",
                    compilation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION)
                        ?: "",
                    hasAudio = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)
                        ?.toBoolean() ?: false,
                    bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                        ?: "",
                    numTracks = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)
                        ?: "",
                )
                emit(metadata)
            } finally {
                retriever.release()
            }
        }.flowOn(Dispatchers.IO)
    }
}