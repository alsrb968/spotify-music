package com.litbig.spotify.core.design.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.litbig.spotify.core.design.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val GoogleFontName = GoogleFont("Prompt")
val Google = FontFamily(
    Font(googleFont = GoogleFontName, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = GoogleFontName, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFontName, fontProvider = provider, weight = FontWeight.Bold)
)

val YoutubeSans = FontFamily(
    Font(R.font.youtube_sans_light, FontWeight.Light),
    Font(R.font.youtube_sans_medium, FontWeight.Normal),
    Font(R.font.youtube_sans_bold, FontWeight.Bold)
)

val Gotham = FontFamily(
    Font(R.font.gotham_light, FontWeight.Light),
    Font(R.font.gotham_book, FontWeight.Normal),
    Font(R.font.gotham_bold, FontWeight.Bold)
)

val ProductSans = FontFamily(
    Font(R.font.product_sans_light, FontWeight.Light),
    Font(R.font.product_sans_regular, FontWeight.Normal),
    Font(R.font.product_sans_bold, FontWeight.Bold)
)

val CircularStd = FontFamily(
    Font(R.font.circular_std_book, FontWeight.Light),
    Font(R.font.circular_std_book, FontWeight.Normal),
    Font(R.font.youtube_sans_bold, FontWeight.Bold)
)

val SpotifyFonts = CircularStd