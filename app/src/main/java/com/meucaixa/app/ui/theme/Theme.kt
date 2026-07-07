package com.meucaixa.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = OnPrimaryLight,
    primaryContainer = PurpleContainerLight,
    onPrimaryContainer = PurplePrimary,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnBackgroundLight,
    surfaceVariant = SurfaceVariantLight,
    error = ExpenseRed,
    errorContainer = ExpenseRedContainer
)

private val DarkColors = darkColorScheme(
    primary = PurplePrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PurpleContainerDark,
    onPrimaryContainer = PurplePrimaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnBackgroundDark,
    surfaceVariant = SurfaceVariantDark,
    error = ExpenseRed,
    errorContainer = ExpenseRedContainer
)

/**
 * Tema principal do Meu Caixa.
 *
 * @param darkTheme se verdadeiro força o tema escuro; por padrão segue o sistema.
 * @param dynamicColor habilita o Material You (cores extraídas do papel de parede) no Android 12+.
 */
@Composable
fun MeuCaixaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MeuCaixaTypography,
        content = content
    )
}
