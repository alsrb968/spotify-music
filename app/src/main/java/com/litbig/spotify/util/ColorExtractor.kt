package com.litbig.spotify.util

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import kotlin.random.Random

object ColorExtractor {

    @JvmStatic
    @Composable
    fun extractDominantColor(imageBitmap: ImageBitmap?): Color {
        var dominantColor by remember { mutableStateOf(Color.Transparent) }

        imageBitmap?.asAndroidBitmap()?.let { bm ->
            Palette.from(bm).generate { palette ->
                palette?.let {
                    // 흰색이나 검은색을 제외한 스와치 필터링
                    val filteredSwatches = it.swatches.filter { swatch ->
                        !isWhiteOrBlackColor(swatch.rgb)
                    }

                    // 필터링된 스와치 중 가장 인구가 많은 색상 선택
                    dominantColor = if (filteredSwatches.isNotEmpty()) {
                        val dominantSwatch = filteredSwatches.maxByOrNull { swatch -> swatch.population }
                        Color(dominantSwatch?.rgb ?: Color.Transparent.toArgb())
                    } else {
                        Color.Transparent // 조건을 만족하는 색상이 없으면 투명 반환
                    }
                }
            }
        }

        return dominantColor
    }

    // RGB 값을 기반으로 흰색이나 검은색인지 확인하는 함수
    private fun isWhiteOrBlackColor(rgb: Int): Boolean {
        val red = (rgb shr 16) and 0xFF
        val green = (rgb shr 8) and 0xFF
        val blue = rgb and 0xFF

        // 흰색으로 간주되는 범위 (거의 밝은 색)
        val isWhite = red > 240 && green > 240 && blue > 240
        // 검은색으로 간주되는 범위 (거의 어두운 색)
        val isBlack = red < 15 && green < 15 && blue < 15

        return isWhite || isBlack
    }

    @JvmStatic
    fun getRandomPastelColor(): Color {
        val hue = Random.nextFloat() * 360 // 0 to 360 for hue
        val saturation = 0.80f // Lower saturation for pastel tone
        val lightness = 0.6f // Higher lightness for a bright pastel color

        return Color.hsl(hue, saturation, lightness)
    }
}