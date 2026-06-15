package com.jdm.alarmlocation.presentation.ui.compose.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

/** Radius tokens from DESIGN_SPEC.md §0 (모양 · 깊이). */
@Immutable
data class AppShapes(
    val card: RoundedCornerShape = RoundedCornerShape(18.dp),
    val dialog: RoundedCornerShape = RoundedCornerShape(22.dp),
    val button: RoundedCornerShape = RoundedCornerShape(14.dp),
    val input: RoundedCornerShape = RoundedCornerShape(12.dp),
    val segment: RoundedCornerShape = RoundedCornerShape(12.dp),
    val chip: RoundedCornerShape = RoundedCornerShape(8.dp),
)

val LocalAppShapes = staticCompositionLocalOf { AppShapes() }
