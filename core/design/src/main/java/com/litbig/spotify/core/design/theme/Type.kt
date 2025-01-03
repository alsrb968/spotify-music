package com.litbig.spotify.core.design.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

val SpotifyTypography = androidx.compose.material3.Typography(
    displayLarge = TextStyle( // Chill Mix
        fontFamily = SpotifyFonts,
        fontSize = 112.sp,
        fontWeight = FontWeight.Bold,
//        letterSpacing = -(0.06).em
    ),
    displayMedium = TextStyle( // Chill Mix
        fontFamily = SpotifyFonts,
        fontSize = 80.sp,
        fontWeight = FontWeight.Bold,
//        letterSpacing = -(0.06).em
    ),
    displaySmall = TextStyle( // Chill Mix
        fontFamily = SpotifyFonts,
        fontSize = 60.sp,
        fontWeight = FontWeight.Bold,
//        letterSpacing = -(0.06).em
    ),

    headlineLarge = TextStyle( // Good evening
        fontFamily = SpotifyFonts,
        fontSize = 38.sp,
        fontWeight = FontWeight.Bold,
//        letterSpacing = (-0.01).em
    ),
    headlineMedium = TextStyle( // Your top mixed
        fontFamily = SpotifyFonts,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
//        letterSpacing = -(0.03).em
    ),
    headlineSmall = TextStyle(
        fontFamily = SpotifyFonts,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
//        letterSpacing = 0.02.em
    ),

    titleLarge = TextStyle( // Daily Mix 3
        fontFamily = SpotifyFonts,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
//        letterSpacing = 0.01.em
    ),
    titleMedium = TextStyle( // Song Title: Ocean Eyes Book 20
        fontFamily = SpotifyFonts,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
//        letterSpacing = 0.01.em
    ),
    titleSmall = TextStyle( // Dur: 2 : 12 = Book 20
        fontFamily = SpotifyFonts,
        fontSize = 20.sp,
//        letterSpacing = -(0.02).em
    ),

    bodyLarge = TextStyle( // Linked Song PL - Bold 18
        fontFamily = SpotifyFonts,
        fontSize = 18.sp,
    ),
    bodyMedium = TextStyle( // Album ayokay Book - 18
        fontFamily = SpotifyFonts,
        fontSize = 18.sp,
        fontWeight = FontWeight.Light,
    ),
    bodySmall = TextStyle( // 2:39
        fontFamily = SpotifyFonts,
        fontSize = 16.sp,
        fontWeight = FontWeight.Light,
    ),

    labelLarge = TextStyle( // Julia Wolf
        fontFamily = SpotifyFonts,
        fontSize = 16.sp,
        fontWeight = FontWeight.Light,
//        letterSpacing = -(0.05).em
    ),
    labelMedium = TextStyle( // 2:39
        fontFamily = SpotifyFonts,
        fontSize = 14.sp,
        fontWeight = FontWeight.Light,
    ),
    labelSmall = TextStyle( // 2:39
        fontFamily = SpotifyFonts,
        fontSize = 12.sp,
        fontWeight = FontWeight.Light,
    ),
)