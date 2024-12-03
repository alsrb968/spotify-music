package com.litbig.spotify.core.domain.usecase.metadata

import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class SyncMetadataUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(
        scope: CoroutineScope,
        files: List<File>,
        onProgress: (Int) -> Unit
    ): Job {
        return scope.launch(Dispatchers.IO) {
            files.forEachIndexed { index, file ->
                onProgress(((index + 1).toFloat() / files.size.toFloat() * 100f).toInt())
                if (musicRepository.isExistMetadata(file.absolutePath)) {
                    return@forEachIndexed
                }
                val metadata = musicRepository.getMusicMetadata(file)
                if (metadata?.artist.isNullOrEmpty() || metadata?.album.isNullOrEmpty()) {
                    return@forEachIndexed
                }
                metadata?.let { musicRepository.insertMetadata(it) }
            }
        }
    }
}