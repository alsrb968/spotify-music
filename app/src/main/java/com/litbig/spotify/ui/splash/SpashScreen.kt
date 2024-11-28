package com.litbig.spotify.ui.splash

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litbig.spotify.R
import com.litbig.spotify.ui.theme.SpotifyTheme
import com.litbig.spotify.ui.tooling.DevicePreviews
import timber.log.Timber

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Timber.i("SplashScreen state: $state")
    when (val s = state) {
        is SplashUiState.Loading ->
            SplashScreen(
                uiState = s,
                navigateToHome = {}
            )

        is SplashUiState.PermissionRequire -> {
            PermissionRequireScreen(
                onPermissionGranted = viewModel::setPermissionGranted
            )
        }

        is SplashUiState.Ready ->
            SplashScreen(
                uiState = s,
                navigateToHome = navigateToHome
            )
    }
}


@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    uiState: SplashUiState,
    navigateToHome: () -> Unit,
) {
    val message = when (uiState) {
        is SplashUiState.Loading -> "로딩 중..."
        is SplashUiState.Ready -> when (uiState.state) {
            "usb" -> "USB를 연결해 주세요."
            "scan" -> "스캔 중..."
            "hash" -> "해시 중..."
            "sync" -> "동기화 중...${uiState.progress}%"
            "done" -> "준비 완료"
            else -> ""
        }
        else -> ""
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.onPrimary),
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Spotify Logo",
        )

        Text(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    if (uiState is SplashUiState.Ready && uiState.state == "done") {
        navigateToHome()
    }
}


@Composable
fun PermissionRequireScreen(
    modifier: Modifier = Modifier,
    onPermissionGranted: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }

    // 권한 요청 런처
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 권한이 허용되면 앱 계속 진행
            permissionGranted = true
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
        onPermissionGranted(true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.onPrimary),
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
        SplashScreen(
            uiState = SplashUiState.Loading,
            navigateToHome = {}
        )
    }
}

@DevicePreviews
@Composable
fun PreviewPermissionRationaleUI() {
    SpotifyTheme {
        PermissionRequireScreen(
            onPermissionGranted = {}
        )
    }
}