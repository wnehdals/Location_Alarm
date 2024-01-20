package com.jdm.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class JdsTypography(
    val H_XL: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        color = Color(0xFF000000)
    ),
    val H_L: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        color = Color(0xFF000000)
    ),
    val H_M: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = Color(0xFF000000)
    ),
    val H_S: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = Color(0xFF000000)
    ),
    val H_XS: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = Color(0xFF000000)
    ),
    val H_XXS: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = Color(0xFF000000)
    ),
    val L_L: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color(0xFF000000)
    ),
    val L_M: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF000000)
    ),
    val L_S: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = Color(0xFF000000)
    ),
    val L_XS: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = Color(0xFF000000)
    ),
    val L_XXS: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        color = Color(0xFF000000)
    ),
    val P_L: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        color = Color(0xFF000000)
    ),
    val P_M: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = Color(0xFF000000)
    ),
    val P_S: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = Color(0xFF000000)
    ),
    val P_XS: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = Color(0xFF000000)
    ),
    val P_XXS: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        color = Color(0xFF000000)
    ),
)
internal val LocalTypography = staticCompositionLocalOf { JdsTypography() }