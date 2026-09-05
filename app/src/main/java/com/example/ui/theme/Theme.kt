package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val XmlDarkColorScheme = darkColorScheme(
    primary = XmlPurple,
    onPrimary = XmlWhite,
    primaryContainer = XmlPurpleDark,
    onPrimaryContainer = XmlWhite,
    secondary = XmlElectricCyan,
    onSecondary = XmlBackground,
    secondaryContainer = XmlSurfaceHighlight,
    onSecondaryContainer = XmlElectricCyan,
    tertiary = XmlSunsetOrange,
    onTertiary = XmlWhite,
    background = XmlBackground,
    onBackground = XmlTextPrimary,
    surface = XmlSurface,
    onSurface = XmlTextPrimary,
    surfaceVariant = XmlSurfaceElevated,
    onSurfaceVariant = XmlTextSecondary,
    outline = XmlBorder,
    outlineVariant = XmlSurfaceHighlight
)

@Composable
fun XmlTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = XmlDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    XmlTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
