package com.litbig.spotify.core.domain.usecase.storage

import com.litbig.spotify.core.domain.repository.StorageRepository
import javax.inject.Inject

class GetStorageHashUseCase @Inject constructor(
    private val storageRepository: StorageRepository
) {
    suspend operator fun invoke(path: String): String? {
        return storageRepository.getStorageHash(path)
    }
}