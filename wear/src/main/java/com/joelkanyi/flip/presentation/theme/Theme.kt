package com.joelkanyi.flip.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

private val FlipColorPalette = Colors(
    primary = FlipColors.HeadsMedium,
    primaryVariant = FlipColors.HeadsDark,
    secondary = FlipColors.TailsMedium,
    secondaryVariant = FlipColors.TailsDark,
    background = FlipColors.Background,
    surface = FlipColors.Background,
    error = FlipColors.TailsDark,
    onPrimary = FlipColors.TextPrimary,
    onSecondary = FlipColors.TextPrimary,
    onBackground = FlipColors.TextPrimary,
    onSurface = FlipColors.TextPrimary,
    onError = FlipColors.TextPrimary
)

@Composable
fun FlipTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = FlipColorPalette,
        content = content
    )
}
