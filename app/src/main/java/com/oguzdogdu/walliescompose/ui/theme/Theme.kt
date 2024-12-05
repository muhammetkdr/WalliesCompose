package com.oguzdogdu.walliescompose.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.oguzdogdu.walliescompose.features.settings.ThemeValues

val DarkColorPalette = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    outline = OutlineDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
)

 val LightColorPalette = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    outline = OutlineLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
)

@Composable
fun WalliesComposeTheme(
    appTheme: ThemeValues,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val targetColorScheme = when (appTheme) {
        ThemeValues.SYSTEM_DEFAULT -> if (darkTheme) DarkColorPalette else LightColorPalette
        ThemeValues.LIGHT_MODE -> LightColorPalette
        ThemeValues.DARK_MODE -> DarkColorPalette
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = targetColorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = rememberAnimatedColorScheme(targetColorScheme),
        typography = Typography,
        content = content
    )
}

@Composable
fun rememberAnimatedColorScheme(currentColorScheme: ColorScheme): ColorScheme {
    var isFirstLaunch by remember { mutableStateOf(true) }
    val animatedSchema: @Composable ColorScheme.() -> ColorScheme = {
        if (isFirstLaunch) {
            isFirstLaunch = false
        }
        animate()
    }
    return currentColorScheme.animatedSchema()
}

@Composable
private fun animateColor(targetValue: Color, durationMillis: Int = 1000): Color {
    return animateColorAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing), label = ""
    ).value
}

@Composable
fun ColorScheme.animate(animationDuration: Int = 1000): ColorScheme {
    return ColorScheme(
        primary = animateColor(primary, animationDuration),
        onPrimary = animateColor(onPrimary, animationDuration),
        primaryContainer = animateColor(primaryContainer, animationDuration),
        onPrimaryContainer = animateColor(onPrimaryContainer, animationDuration),
        inversePrimary = animateColor(inversePrimary, animationDuration),
        secondary = animateColor(secondary, animationDuration),
        onSecondary = animateColor(onSecondary, animationDuration),
        secondaryContainer = animateColor(secondaryContainer, animationDuration),
        onSecondaryContainer = animateColor(onSecondaryContainer, animationDuration),
        tertiary = animateColor(tertiary, animationDuration),
        onTertiary = animateColor(onTertiary, animationDuration),
        tertiaryContainer = animateColor(tertiaryContainer, animationDuration),
        onTertiaryContainer = animateColor(onTertiaryContainer, animationDuration),
        background = animateColor(background, animationDuration),
        onBackground = animateColor(onBackground, animationDuration),
        surface = animateColor(surface, animationDuration),
        onSurface = animateColor(onSurface, animationDuration),
        surfaceVariant = animateColor(surfaceVariant, animationDuration),
        onSurfaceVariant = animateColor(onSurfaceVariant, animationDuration),
        surfaceTint = animateColor(surfaceTint, animationDuration),
        inverseSurface = animateColor(inverseSurface, animationDuration),
        inverseOnSurface = animateColor(inverseOnSurface, animationDuration),
        error = animateColor(error, animationDuration),
        onError = animateColor(onError, animationDuration),
        errorContainer = animateColor(errorContainer, animationDuration),
        onErrorContainer = animateColor(onErrorContainer, animationDuration),
        outline = animateColor(outline, animationDuration),
        outlineVariant = animateColor(outlineVariant, animationDuration),
        scrim = animateColor(scrim, animationDuration),
        surfaceBright = animateColor(surfaceBright, animationDuration),
        surfaceDim = animateColor(surfaceDim, animationDuration),
        surfaceContainer = animateColor(surfaceContainer, animationDuration),
        surfaceContainerHigh = animateColor(surfaceContainerHigh, animationDuration),
        surfaceContainerHighest = animateColor(surfaceContainerHighest, animationDuration),
        surfaceContainerLow = animateColor(surfaceContainerLow, animationDuration),
        surfaceContainerLowest = animateColor(surfaceContainerLowest, animationDuration),
    )
}
