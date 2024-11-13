package com.litbig.spotify.core.data.datasource.local

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import com.litbig.spotify.core.data.mapper.local.MusicMetadataMapper.toDuration
import com.litbig.spotify.core.domain.model.local.MusicMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.io.File
import javax.inject.Inject

interface MediaRetrieverDataSource {
    suspend fun getAlbumArt(file: File): Flow<Bitmap?>
    suspend fun getAlbumArtList(files: List<File>): Flow<List<Bitmap?>>
    suspend fun getMusicMetadata(file: File): Flow<MusicMetadata?>
    suspend fun getMusicMetadataList(files: List<File>): Flow<List<MusicMetadata?>>
}

class MediaRetrieverDataSourceImpl @Inject constructor() : MediaRetrieverDataSource {
    override suspend fun getAlbumArt(file: File): Flow<Bitmap?> {
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

    override suspend fun getAlbumArtList(files: List<File>): Flow<List<Bitmap?>> =
        flow {
            val bitmapList = MutableList<Bitmap?>(files.size) { null }

            files.forEachIndexed { index, file ->
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(file.absolutePath)
                    val albumArt = retriever.embeddedPicture?.let {
                        BitmapFactory.decodeByteArray(it, 0, it.size)
                    }
                    bitmapList[index] = albumArt
                } catch (e: Exception) {
                    e.printStackTrace()
                    bitmapList[index] = null // 실패 시 null 설정
                } finally {
                    retriever.release() // 리소스 해제
                }

                // 각 파일 처리 후 리스트 방출
                emit(bitmapList.toList())
            }
        }.flowOn(Dispatchers.IO) // I/O 작업을 비동기적으로 처리

    override suspend fun getMusicMetadata(file: File): Flow<MusicMetadata?> {
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

    override suspend fun getMusicMetadataList(files: List<File>): Flow<List<MusicMetadata?>> =
        flow {
            val metadataList = MutableList<MusicMetadata?>(files.size) { null }

            files.forEachIndexed { index, file ->
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
                    metadataList[index] = metadata
                } catch (e: Exception) {
                    e.printStackTrace()
                    metadataList[index] = null // 실패 시 null 설정
                } finally {
                    retriever.release() // 리소스 해제
                }

                // 각 파일 처리 후 리스트 방출
                emit(metadataList.toList())
            }
        }.flowOn(Dispatchers.IO) // I/O 작업을 비동기적으로 처리
}