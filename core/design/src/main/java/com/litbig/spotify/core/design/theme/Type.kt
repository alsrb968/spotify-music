package com.litbig.spotify.core.design.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

val SpotifyTypography = androidx.compose.material3.Typography(
    headlineLarge = TextStyle( // Good evening
        fontFamily = ProductSans,
        fontSize = 38.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.01).em
    ),
    headlineMedium = TextStyle( // Your top mixed
        fontFamily = ProductSans,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = -(0.03).em
    ),
    headlineSmall = TextStyle(
        fontFamily = ProductSans,
        fontSize = 22.sp,
        letterSpacing = 0.02.em
    ),

    titleLarge = TextStyle( // Daily Mix 3
        fontFamily = ProductSans,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.01.em
    ),
    titleMedium = TextStyle( // Song Title: Ocean Eyes Book 20
        fontFamily = ProductSans,
        fontSize = 20.sp,
        letterSpacing = 0.01.em
    ),
    titleSmall = TextStyle( // Dur: 2 : 12 = Book 20
        fontFamily = ProductSans,
        fontSize = 20.sp,
        letterSpacing = -(0.02).em
    ),

    bodyLarge = TextStyle( // Linked Song PL - Bold 18
        fontFamily = ProductSans,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    ),
    bodyMedium = TextStyle( // Album ayokay Book - 18
        fontFamily = ProductSans,
        fontSize = 18.sp,
    ),
    bodySmall = TextStyle( // 2:39
        fontFamily = ProductSans,
        fontSize = 16.sp,
    ),

    labelLarge = TextStyle( // Julia Wolf
        fontFamily = ProductSans,
        fontSize = 16.sp,
        letterSpacing = -(0.05).em
    ),
)