package com.litbig.spotify.core.domain.usecase

import com.litbig.spotify.core.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class SyncMetadataUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(files: List<File>) {
        Timber.i("SyncMetadataUseCase: files.size: ${files.size}")
        withContext(Dispatchers.IO) {
            files.forEach { file ->
                if (musicRepository.isExistMetadata(file.absolutePath)) {
                    return@forEach
                }
                val metadata = musicRepository.getMusicMetadataWithoutFlow(file)
                metadata?.let {
                    musicRepository.insertMetadata(it)
                    Timber.i("SyncMetadataUseCase: Metadata inserted: ${file.absolutePath}")
                }
            }
        }
    }
}