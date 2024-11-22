package com.litbig.spotify.core.domain.usecase

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
        onProgress: (Int, Int) -> Unit
    ): Job {
        return scope.launch(Dispatchers.IO) {
            files.forEachIndexed { index, file ->
                if (musicRepository.isExistMetadata(file.absolutePath)) {
                    return@forEachIndexed
                }
                val metadata = musicRepository.getMusicMetadata(file)
                metadata?.let {
                    musicRepository.insertMetadata(it)
                    onProgress(index + 1, files.size)
                }
            }
        }
    }
}