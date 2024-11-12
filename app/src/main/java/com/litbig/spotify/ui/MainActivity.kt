package com.litbig.spotify.ui

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.litbig.spotify.ui.grid.GridScreen
import com.litbig.spotify.ui.list.ListScreen
import com.litbig.spotify.ui.shared.FooterExpanded
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.util.FileExtensions.getMusicMapByAlbum
import com.litbig.spotify.util.FileExtensions.getMusicMetadata
import com.litbig.spotify.util.FileExtensions.scanForMusicFiles
import com.litbig.spotify.util.UsbReceiver
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.i("onCreate")
        checkAndRequestStoragePermission()

        setContent {
            SpotifyTheme {
                val musicFiles = remember { mutableStateListOf<File>() }
                val isUsb1Detected = remember { mutableStateOf(false) }
                val isUsb2Detected = remember { mutableStateOf(false) }

                val usb1 = File("/storage/usbdisk1")
                val usb2 = File("/storage/usbdisk2")
                if (usb1.exists() && !isUsb1Detected.value) {
                    musicFiles.addAll(usb1.scanForMusicFiles())
                    Timber.i("Music file count: ${musicFiles.size}")
                    isUsb1Detected.value = true
                }
                if (usb2.exists() && !isUsb2Detected.value) {
                    musicFiles.addAll(usb2.scanForMusicFiles())
                    Timber.i("Music file count: ${musicFiles.size}")
                    isUsb2Detected.value = true
                }

                val usbReceiver = rememberUpdatedState(newValue = UsbReceiver(
                    onUsbMounted = { path ->
                        Timber.i("USB mounted: $path")
                        when (path) {
                            usb1.absolutePath -> {
                                if (isUsb1Detected.value) return@UsbReceiver
                                musicFiles.addAll(usb1.scanForMusicFiles())
                                isUsb1Detected.value = true
                            }
                            usb2.absolutePath -> {
                                if (isUsb2Detected.value) return@UsbReceiver
                                musicFiles.addAll(usb2.scanForMusicFiles())
                                isUsb2Detected.value = true
                            }
                        }
                    },
                    onUsbEjected = { path ->
                        Timber.i("USB ejected: $path")
                        musicFiles.removeAll { it.absolutePath.startsWith(path) }
                    }
                ))

                DisposableEffect(Unit) {
                    registerReceiver(usbReceiver.value, IntentFilter().apply {
                        addAction(Intent.ACTION_MEDIA_MOUNTED)
                        addAction(Intent.ACTION_MEDIA_EJECT)
                        addDataScheme("file")
                    })

                    onDispose {
                        unregisterReceiver(usbReceiver.value)
                    }
                }

//                val albumMap by musicFiles.getMusicMapByAlbum().collectAsState(initial = emptyMap())

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {



                    ListScreen(
                        musicFiles = musicFiles
                    )
//                    GridScreen(
//                        musicFiles = musicFiles
//                    )

                    FooterExpanded(
                        modifier = Modifier
                            .align(Alignment.BottomStart),
                        musicMetadata = musicFiles[0].getMusicMetadata(),
                        playingTime = 10000,
                        isFavorite = false,
                        onClick = { }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 권한이 허용된 경우 수행할 작업
                onStoragePermissionGranted()
            } else {
                // 권한이 거부된 경우 사용자에게 알림
                Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkAndRequestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 이미 허용된 경우
            onStoragePermissionGranted()
        } else {
            // 권한이 허용되지 않은 경우 권한 요청
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun onStoragePermissionGranted() {
        // 권한이 허용되었을 때 수행할 작업
        Toast.makeText(this, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
    }
}