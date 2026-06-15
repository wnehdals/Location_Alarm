package com.jdm.alarmlocation.presentation.ui.compose.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.dp

/**
 * Root theme. Design direction is "Clean Friendly (Light)" per DESIGN_SPEC.md —
 * we intentionally do not auto-switch to dark mode (full-screen alarm 06 is the only dark surface).
 *
 * Access tokens through [AppTheme.colors], [AppTheme.typography], [AppTheme.shapes].
 */
@Composable
fun AlarmLocationTheme(content: @Composable () -> Unit) {
    val colors = LightAppColors
    val typography = AppTypography()
    val shapes = AppShapes()

    val materialColors = lightColorScheme(
        primary = colors.primary,
        onPrimary = colors.onPrimary,
        background = colors.background,
        onBackground = colors.text,
        surface = colors.surface,
        onSurface = colors.text,
        error = colors.danger,
        outline = colors.border,
    )

    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppTypography provides typography,
        LocalAppShapes provides shapes,
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = materialTypography(typography),
            shapes = Shapes(
                small = RoundedCornerShape(8.dp),
                medium = RoundedCornerShape(14.dp),
                large = RoundedCornerShape(18.dp),
            ),
            content = content,
        )
    }
}

object AppTheme {
    val colors: AppColors
        @Composable @ReadOnlyComposable get() = LocalAppColors.current
    val typography: AppTypography
        @Composable @ReadOnlyComposable get() = LocalAppTypography.current
    val shapes: AppShapes
        @Composable @ReadOnlyComposable get() = LocalAppShapes.current
}
