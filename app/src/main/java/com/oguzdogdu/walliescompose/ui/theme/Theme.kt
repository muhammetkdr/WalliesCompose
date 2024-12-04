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
    appTheme: String?,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val targetColorScheme = when (appTheme) {
        ThemeValues.SYSTEM_DEFAULT.title -> if (darkTheme) DarkColorPalette else LightColorPalette
        ThemeValues.LIGHT_MODE.title -> LightColorPalette
        ThemeValues.DARK_MODE.title -> DarkColorPalette
        null -> if (darkTheme) DarkColorPalette else LightColorPalette
        else -> if (darkTheme) DarkColorPalette else LightColorPalette
    }

    var isFirstLaunch by remember { mutableStateOf(true) }

    val animatedColorScheme = if (isFirstLaunch) {
        isFirstLaunch = false
        targetColorScheme
    } else {
        targetColorScheme.animate()
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = animatedColorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = animatedColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun ColorScheme.animate(
    animationDuration: Int = 1000
): ColorScheme {
    val primary by animateColorAsState(primary, tween(animationDuration, easing = LinearEasing))
    val onPrimary by animateColorAsState(onPrimary, tween(animationDuration, easing = LinearEasing))
    val primaryContainer by animateColorAsState(primaryContainer, tween(animationDuration, easing = LinearEasing))
    val onPrimaryContainer by animateColorAsState(onPrimaryContainer, tween(animationDuration, easing = LinearEasing))
    val inversePrimary by animateColorAsState(inversePrimary, tween(animationDuration, easing = LinearEasing))
    val secondary by animateColorAsState(secondary, tween(animationDuration, easing = LinearEasing))
    val onSecondary by animateColorAsState(onSecondary, tween(animationDuration, easing = LinearEasing))
    val secondaryContainer by animateColorAsState(secondaryContainer, tween(animationDuration, easing = LinearEasing))
    val onSecondaryContainer by animateColorAsState(onSecondaryContainer, tween(animationDuration, easing = LinearEasing))
    val tertiary by animateColorAsState(tertiary, tween(animationDuration, easing = LinearEasing))
    val onTertiary by animateColorAsState(onTertiary, tween(animationDuration, easing = LinearEasing))
    val tertiaryContainer by animateColorAsState(tertiaryContainer, tween(animationDuration, easing = LinearEasing))
    val onTertiaryContainer by animateColorAsState(onTertiaryContainer, tween(animationDuration, easing = LinearEasing))
    val background by animateColorAsState(background, tween(animationDuration, easing = LinearEasing))
    val onBackground by animateColorAsState(onBackground, tween(animationDuration, easing = LinearEasing))
    val surface by animateColorAsState(surface, tween(animationDuration, easing = LinearEasing))
    val onSurface by animateColorAsState(onSurface, tween(animationDuration, easing = LinearEasing))
    val surfaceVariant by animateColorAsState(surfaceVariant, tween(animationDuration, easing = LinearEasing))
    val onSurfaceVariant by animateColorAsState(onSurfaceVariant, tween(animationDuration, easing = LinearEasing))
    val surfaceTint by animateColorAsState(surfaceTint, tween(animationDuration, easing = LinearEasing))
    val inverseSurface by animateColorAsState(inverseSurface, tween(animationDuration, easing = LinearEasing))
    val inverseOnSurface by animateColorAsState(inverseOnSurface, tween(animationDuration, easing = LinearEasing))
    val error by animateColorAsState(error, tween(animationDuration, easing = LinearEasing))
    val onError by animateColorAsState(onError, tween(animationDuration, easing = LinearEasing))
    val errorContainer by animateColorAsState(errorContainer, tween(animationDuration, easing = LinearEasing))
    val onErrorContainer by animateColorAsState(onErrorContainer, tween(animationDuration, easing = LinearEasing))
    val outline by animateColorAsState(outline, tween(animationDuration, easing = LinearEasing))
    val outlineVariant by animateColorAsState(outlineVariant, tween(animationDuration, easing = LinearEasing))
    val scrim by animateColorAsState(scrim, tween(animationDuration, easing = LinearEasing))

    return ColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        inversePrimary = inversePrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        surfaceTint = surfaceTint,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        error = error,
        onError = onError,
        errorContainer = errorContainer,
        onErrorContainer = onErrorContainer,
        outline = outline,
        outlineVariant = outlineVariant,
        scrim = scrim
    )
}