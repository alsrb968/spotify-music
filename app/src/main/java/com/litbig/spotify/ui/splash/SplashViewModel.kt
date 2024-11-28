package com.litbig.spotify.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.extension.combine
import com.litbig.spotify.core.domain.usecase.metadata.SyncMetadataUseCase
import com.litbig.spotify.core.domain.usecase.storage.AddStorageHashUseCase
import com.litbig.spotify.core.domain.usecase.storage.GetStorageHashUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data object PermissionRequire : SplashUiState
    data class Ready(
        val state: String,
        val progress: Int,
    ) : SplashUiState
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val syncMetadataUseCase: SyncMetadataUseCase,
    private val addStorageHashUseCase: AddStorageHashUseCase,
    private val getStorageHashUseCase: GetStorageHashUseCase,
) : ViewModel() {

    private val isUsb2DetectedFlow = MutableStateFlow(File(USB2).exists())
    private val isPermissionGrantedFlow = MutableStateFlow(false)
    private val isPossibleScanFlow = combine(
        isUsb2DetectedFlow,
        isPermissionGrantedFlow,
    ) { isDetect, isPermission ->
        Timber.d("isDetect: $isDetect, isPermission: $isPermission")
        isDetect && isPermission
    }
    private val isScanFlow = MutableStateFlow(false)
    private val isHashFlow = MutableStateFlow(false)
    private val syncProgressFlow = MutableStateFlow(0)
    private val isReadyFlow = MutableStateFlow(false)



    val state: StateFlow<SplashUiState> = combine(
        isUsb2DetectedFlow,
        isPermissionGrantedFlow,
        isScanFlow,
        isHashFlow,
        syncProgressFlow,
        isReadyFlow
    ) { isUsbDetect, isPermission, isScan, isHash, sync, isReady ->
        Timber.w("isUsbDetect: $isUsbDetect, isPermission: $isPermission, isScan: $isScan, isHash: $isHash, sync: $sync, isReady: $isReady")

        when {
            isPermission.not() -> SplashUiState.PermissionRequire
            isUsbDetect.not() -> SplashUiState.Ready("usb", -1)
            isScan.not() -> SplashUiState.Ready("scan", -1)
            isHash.not() -> SplashUiState.Ready("hash", -1)
            isReady -> SplashUiState.Ready("done", -1)
            sync >= 0 -> SplashUiState.Ready("sync", sync)
            else -> throw IllegalStateException("Invalid state")
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SplashUiState.Loading
    )


    private var syncJob: Job? = null

    init {
        Timber.d("init")
        viewModelScope.launch {
            isPossibleScanFlow.collectLatest {
                syncJob?.cancel()
                if (!it) return@collectLatest

                val files = STORAGE.scanMusicFiles()
                isScanFlow.value = true

                val hash = files.calculateHash()
                val storedHash = getStorageHashUseCase(USB2)

                if (hash != storedHash) {
                    addStorageHashUseCase(USB2, hash)

                    syncJob = syncMetadataUseCase(
                        scope = viewModelScope,
                        files = files,
                    ) { progress ->
                        syncProgressFlow.value = progress
                        if (progress == 100) {
                            isReadyFlow.value = true
                        }
                    }
                } else {
                    Timber.d("hash is same")
                    isReadyFlow.value = true
                }

                isHashFlow.value = true
            }
        }
    }

    fun setPermissionGranted(isGranted: Boolean) {
        Timber.w("isGranted: $isGranted")
        isPermissionGrantedFlow.value = isGranted
    }

    fun setUsbDetected(path: String, isDetected: Boolean) {
        Timber.w("path: $path, isDetected: $isDetected")
        when (path) {
            USB2 -> isUsb2DetectedFlow.value = isDetected
        }
    }

    private suspend fun String.scanMusicFiles(): List<File> {
        return withContext(Dispatchers.IO) {
            File(this@scanMusicFiles).walk().filter { file ->
                file.isFile &&
                        file.name.startsWith("_").not() &&
                        file.name.startsWith(".").not() &&
                        file.extension.lowercase() in listOf("mp3", "flac", "wav", "m4a")
            }.toList()
        }
    }

    private fun String.toSHA256Hash(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(this.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun List<File>.calculateHash(): String {
        val hashes = this.map { it.name.toSHA256Hash() }
        val combinedHashString = hashes.joinToString("")
        return combinedHashString.toSHA256Hash()
    }

    companion object {
        private const val USB1 = "/storage/usbdisk1"
        private const val USB2 = "/storage/usbdisk2"

        private const val STORAGE = USB2
    }
}