package com.pivoto.simplesms.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Green_500,
    primaryVariant = Green_700,
    secondary = Green_100,
    secondaryVariant = Green_100
)

private val LightColorPalette = lightColors(
    primary = Green_500,
    primaryVariant = Green_700,
    secondary = Green_100,
    secondaryVariant = Green_100
)

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
