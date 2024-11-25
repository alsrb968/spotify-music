package com.litbig.spotify.ui.splash

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.litbig.spotify.R
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navigateToGrid: () -> Unit
) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var shouldExitApp by remember { mutableStateOf(false) } // 앱 종료 여부 상태 추가

    // 권한 요청 런처
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 권한이 허용되면 앱 계속 진행
            permissionGranted = true
        } else {
            // 권한이 거부되면 Rationale 여부 확인
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // "다시 묻지 않기" 체크된 상태
                shouldExitApp = true
            } else {
                // 권한을 단순히 거부한 상태, 다시 요청 가능
                showPermissionRationale = true
            }
        }
    }

    // 초기 권한 상태 확인
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionGranted = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    // 권한 상태에 따른 처리
    if (permissionGranted) {
        viewModel.setPermissionGranted(true)

        LaunchedEffect(Unit) {
            viewModel.scanProgress.collectLatest { progress ->
                if (progress.second > 0 &&
                    progress.first == progress.second
                ) {
                    snackbarHostState.showSnackbar("동기화 완료. 메인 화면으로 이동합니다.")
                    delay(1000L)
                    navigateToGrid()
                }
            }
        }

        val scanProgress = viewModel.scanProgress.collectAsState()
        if (scanProgress.value.second > 0) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 250.dp),
                    text = "동기화 중 ... ${scanProgress.value.first}/${scanProgress.value.second}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    } else if (shouldExitApp) {
        // "다시 묻지 않기" 상태에서 앱 종료
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar("권한 설정이 필요합니다. 앱을 종료합니다.")
            (context as Activity).finish() // 앱 종료
        }
    } else if (showPermissionRationale) {
        // 권한 요청 설명 UI 표시
        PermissionRationaleUI(requestPermissionLauncher, snackbarHostState)
    } else {
        // 권한 확인 중 상태 유지
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 250.dp),
                text = "권한을 확인 중입니다...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Spotify Logo",
        )

        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                modifier = Modifier,
                snackbarData = data
            )
        }
    }
}

@Composable
fun PermissionRationaleUI(
    requestPermissionLauncher: ActivityResultLauncher<String>,
    snackbarHostState: SnackbarHostState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "이 권한이 필요합니다. 허용해 주세요.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }) {
            Text(
                text = "권한 요청",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@DevicePreviews
@Composable
fun PreviewSplashScreen() {
    SpotifyTheme {
        SplashScreen(navigateToGrid = {})
    }
}

@DevicePreviews
@Composable
fun PreviewPermissionRationaleUI() {
    SpotifyTheme {
        PermissionRationaleUI(
            requestPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}