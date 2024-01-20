package com.jdm.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun JdsTheme(
    colors: JdsColors = JdsTheme.colors,
    typography: JdsTypography = JdsTheme.typography,
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(
        LocalColors provides colors,
    ) {
        content()
    }
}

object JdsTheme {
    val colors: JdsColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current
    val typography: JdsTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}