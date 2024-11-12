package com.litbig.spotify.util

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object FileExtensions {
    @JvmStatic
    fun File.scanForMusicFiles(): List<File> {
        val musicFiles = mutableListOf<File>()
        val supportedExtensions = arrayOf("mp3")

        listFiles()?.forEach { file ->
            if (file.isDirectory && !file.name.startsWith("_") && !file.name.startsWith(".")) {
                musicFiles.addAll(file.scanForMusicFiles())
            } else if (supportedExtensions.any { file.name.endsWith(it, ignoreCase = true) }) {
                musicFiles.add(file)
            }
        }
        return musicFiles
    }

    @JvmStatic
    fun File.getMusicMetadata(): MusicMetadata {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(absolutePath)

        val albumArt = retriever.embeddedPicture?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap()
        }

        val metadata = MusicMetadata(
            absolutePath = absolutePath,
            title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: nameWithoutExtension,
            artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "",
            album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "",
            genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: "",
            albumArt = albumArt,
            duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L,
            year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR) ?: "",
            albumArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST) ?: "",
            composer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER) ?: "",
            writer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER) ?: "",
            cdTrackNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER) ?: "",
            discNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER) ?: "",
            date = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE) ?: "",
            mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: "",
            compilation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION) ?: "",
            hasAudio = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)?.toBoolean() ?: false,
            bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE) ?: "",
            numTracks = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS) ?: ""
        )

        retriever.release()

        return metadata
    }

    @JvmStatic
    fun List<File>.getMusicMapByAlbum(): Flow<Map<String, List<File>>> = flow {
        val albumMap = ConcurrentHashMap<String, MutableList<File>>()

        for (file in this@getMusicMapByAlbum) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(file.absolutePath)
                val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Unknown"

                synchronized(albumMap) {
                    albumMap.computeIfAbsent(album) { mutableListOf() }.add(file)
                }

                // 방출할 복사본 생성
                val snapshot = albumMap.mapValues { it.value.toList() }.toMap()
                emit(snapshot)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                retriever.release()
            }
        }
    }.flowOn(Dispatchers.IO)
}