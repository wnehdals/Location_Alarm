package com.jdm.alarmlocation.presentation.ui.compose.theme

import androidx.compose.ui.graphics.Color

/**
 * Design tokens from DESIGN_SPEC.md §0 (Design Tokens).
 * Single source of truth for the Compose color palette.
 */
object AppPalette {
    val Primary = Color(0xFF4F6CF7)
    val PrimaryTint = Color(0xFFEEF1FE)

    val Success = Color(0xFF22C55E)
    val SuccessText = Color(0xFF1FA463)

    val Warning = Color(0xFFF59E0B)
    val WarningText = Color(0xFFC77700)

    val Danger = Color(0xFFEF4444)

    val Text = Color(0xFF1A1D23)
    val TextSub = Color(0xFF6B7280)
    val Border = Color(0xFFE5E8EC)
    val Background = Color(0xFFF4F6F8)
    val Surface = Color(0xFFFFFFFF)
    val Scrim = Color(0xFF1F2637)

    val OnPrimary = Color(0xFFFFFFFF)

    // Brand-specific
    val KakaoYellow = Color(0xFFFEE500)
    val KakaoLabel = Color(0xFF191600)
}
