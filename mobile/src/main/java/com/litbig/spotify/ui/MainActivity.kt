package com.litbig.spotify.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsControllerCompat
import com.litbig.spotify.ui.theme.SpotifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setTransparentStatusBar()
        setContent {
            SpotifyTheme {
                SpotifyApp()
            }
        }
    }

    private fun setTransparentStatusBar() {
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        window.statusBarColor = Color.parseColor("#80000000") // 검은 반투명
        insetsController.isAppearanceLightStatusBars = false // 상태 표시줄 아이콘 흰색
    }
}