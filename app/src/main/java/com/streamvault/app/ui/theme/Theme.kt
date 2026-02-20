package com.streamvault.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    surface = Surface,
    onSurface = TextSecondary,
    background = Background,
    onBackground = TextPrimary,
    error = ErrorColor,
    onError = OnPrimary
)

@Composable
fun StreamVaultTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = StreamVaultTypography,
            content = content
        )
    }
}
