package com.litbig.spotify.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litbig.spotify.core.domain.usecase.SyncMetadataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val syncMetadataUseCase: SyncMetadataUseCase
) : ViewModel() {

    private val _isPermissionGranted = MutableStateFlow(false)
    val isPermissionGranted = _isPermissionGranted

    private val _isUsb2DetectedFlow = MutableStateFlow(File(USB2).exists())
    val isUsb2DetectedFlow = _isUsb2DetectedFlow

    val isPossibleScanFlow = combine(
        _isUsb2DetectedFlow,
        _isPermissionGranted
    ) { isDetect, isPermission ->
        Timber.d("isDetect: $isDetect, isPermission: $isPermission")
        isDetect && isPermission
    }

    private val _scanProgress = MutableStateFlow(0 to 0)
    val scanProgress = _scanProgress

    private var syncJob: Job? = null

    init {
        Timber.d("init")
        viewModelScope.launch {
            isPossibleScanFlow.collectLatest {
                syncJob?.cancel()
                if (it) {
                    syncJob = syncMetadata(path = USB2)
                }
            }
        }
    }

    fun setPermissionGranted(isGranted: Boolean) {
        _isPermissionGranted.value = isGranted
    }

    fun setUsb2Detected(isDetected: Boolean) {
        Timber.d("isDetected: $isDetected")
        _isUsb2DetectedFlow.value = isDetected
    }

    fun syncMetadata(path: String): Job = viewModelScope.launch {
        val files = scanForMusicFiles(path)
        Timber.i("scanForMusicFiles size=${files.size}")

        syncMetadataUseCase(
            scope = viewModelScope,
            files = files,
        ) { scanned, total ->
            _scanProgress.value = scanned to total
        }
    }

    private suspend fun scanForMusicFiles(path: String): List<File> {
        return withContext(Dispatchers.IO) {
            File(path).walk().filter { file ->
                file.isFile &&
                        file.name.startsWith("_").not() &&
                        file.name.startsWith(".").not() &&
                        file.extension.lowercase() in listOf("mp3")
            }.toList()
        }
    }

    companion object {
        private const val USB1 = "/storage/usbdisk1"
        private const val USB2 = "/storage/usbdisk2"
    }
}