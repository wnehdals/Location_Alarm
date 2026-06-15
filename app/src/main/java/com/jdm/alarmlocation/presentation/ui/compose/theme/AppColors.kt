package com.jdm.alarmlocation.presentation.ui.compose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic color set for the app, richer than Material3's [androidx.compose.material3.ColorScheme].
 * Access via [com.jdm.alarmlocation.presentation.ui.compose.theme.AppTheme.colors].
 */
@Immutable
data class AppColors(
    val primary: Color,
    val primaryTint: Color,
    val success: Color,
    val successText: Color,
    val warning: Color,
    val warningText: Color,
    val danger: Color,
    val text: Color,
    val textSub: Color,
    val border: Color,
    val background: Color,
    val surface: Color,
    val scrim: Color,
    val onPrimary: Color,
)

val LightAppColors = AppColors(
    primary = AppPalette.Primary,
    primaryTint = AppPalette.PrimaryTint,
    success = AppPalette.Success,
    successText = AppPalette.SuccessText,
    warning = AppPalette.Warning,
    warningText = AppPalette.WarningText,
    danger = AppPalette.Danger,
    text = AppPalette.Text,
    textSub = AppPalette.TextSub,
    border = AppPalette.Border,
    background = AppPalette.Background,
    surface = AppPalette.Surface,
    scrim = AppPalette.Scrim,
    onPrimary = AppPalette.OnPrimary,
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }
