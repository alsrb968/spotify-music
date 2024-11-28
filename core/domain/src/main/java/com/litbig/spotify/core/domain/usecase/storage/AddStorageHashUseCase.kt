package com.litbig.spotify.core.domain.usecase.storage

import com.litbig.spotify.core.domain.repository.StorageRepository
import javax.inject.Inject

class AddStorageHashUseCase @Inject constructor(
    private val storageRepository: StorageRepository
) {
    suspend operator fun invoke(path: String, hash: String) {
        storageRepository.addStorageHash(path, hash)
    }
}