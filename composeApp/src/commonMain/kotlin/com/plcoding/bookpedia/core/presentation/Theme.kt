package com.plcoding.bookpedia.core.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SandYellow,
    background = DarkBlue,
    surface = DarkBlue,
    onPrimary = DarkBlue,
    onBackground = DesertWhite,
    onSurface = DesertWhite,
    secondary = LightBlue
)

private val LightColorScheme = lightColorScheme(
    primary = DarkBlue,
    background = DesertWhite,
    surface = DesertWhite,
    onPrimary = DesertWhite,
    onBackground = DarkBlue,
    onSurface = DarkBlue,
    secondary = SandYellow
)

@Composable
fun BookpediaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
