package com.litbig.spotify.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlin.random.Random

suspend fun extractDominantColorFromUrl(context: Context, imageUrl: String?): Color {
    if (imageUrl.isNullOrEmpty()) return Color.Transparent

    // ImageLoader 생성
    val imageLoader = ImageLoader(context)

    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .allowHardware(false) // 하드웨어 비트맵 비활성화
        .build()

    val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
    val bitmap = (result as? BitmapDrawable)?.bitmap ?: return Color.Transparent

    // 하드웨어 비트맵 변환
    val safeBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
        bitmap.copy(Bitmap.Config.ARGB_8888, false)
    } else {
        bitmap
    }

    // Palette로 Dominant Color 추출
    val palette = Palette.from(safeBitmap).generate()

    // 검은색을 제외한 스와치 필터링
    val filteredSwatches = palette.swatches.filter { swatch ->
        !isBlackColor(swatch.rgb)
    }

    // 필터링된 스와치 중 가장 인구가 많은 색상 선택
    val dominantColor = if (filteredSwatches.isNotEmpty()) {
        val dominantSwatch = filteredSwatches.maxByOrNull { swatch -> swatch.population }
        Color(dominantSwatch?.rgb ?: Color.Transparent.toArgb())
    } else {
        Color.Transparent // 조건을 만족하는 색상이 없으면 투명 반환
    }

    return dominantColor
}

@Composable
fun extractDominantColor(imageBitmap: ImageBitmap?): Color {
    var dominantColor by remember { mutableStateOf(Color.Transparent) }

    imageBitmap?.asAndroidBitmap()?.let { bm ->
        Palette.from(bm).generate { palette ->
            palette?.let {
                // 검은색을 제외한 스와치 필터링
                val filteredSwatches = it.swatches.filter { swatch ->
                    !isBlackColor(swatch.rgb)
                }

                // 필터링된 스와치 중 가장 인구가 많은 색상 선택
                dominantColor = if (filteredSwatches.isNotEmpty()) {
                    val dominantSwatch =
                        filteredSwatches.maxByOrNull { swatch -> swatch.population }
                    Color(dominantSwatch?.rgb ?: Color.Transparent.toArgb())
                } else {
                    Color.Transparent // 조건을 만족하는 색상이 없으면 투명 반환
                }
            }
        }
    }

    return dominantColor
}

private fun isBlackColor(rgb: Int, threshold: Int = 50): Boolean {
    val red = (rgb shr 16) and 0xFF
    val green = (rgb shr 8) and 0xFF
    val blue = rgb and 0xFF

    // 밝기 계산 (가중치를 사용하여 사람이 보는 밝기에 더 가깝게 측정)
    val luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue

    // 밝기가 threshold 이하일 경우 검은색 계열로 판단
    return luminance < threshold
}

fun getRandomPastelColor(): Color {
    val hue = Random.nextFloat() * 360 // 0 to 360 for hue
    val saturation = 0.80f // Lower saturation for pastel tone
    val lightness = 0.6f // Higher lightness for a bright pastel color

    return Color.hsl(hue, saturation, lightness)
}

fun Color.darkenColor(factor: Float): Color {
    return Color(
        red = (this.red * factor).coerceIn(0f, 1f),
        green = (this.green * factor).coerceIn(0f, 1f),
        blue = (this.blue * factor).coerceIn(0f, 1f),
        alpha = this.alpha
    )
}