package com.example.dailybrew.ui.theme

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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CoffeeBrown,
    secondary = CoffeeLightBrown,
    tertiary = CoffeeBrown,
    background = CremeBg,
    surface = CremeBg,
    onPrimary = CremeBg,
    onSecondary = CoffeeBrown,
    onBackground = CoffeeBrown,
    onSurface = CoffeeBrown
)

private val LightColorScheme = lightColorScheme(
    primary = CoffeeBrown,
    secondary = CoffeeLightBrown,
    tertiary = CoffeeBrown,
    background = CremeBg,
    surface = CremeBg,
    onPrimary = CremeBg,
    onSecondary = CoffeeBrown,
    onBackground = CoffeeBrown,
    onSurface = CoffeeBrown
)


@Composable
fun DailyBrewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Use a solid dark color for status bar to ensure visibility of white text/icons
            val darkBrown = Color(0xFF5D3A25).toArgb()

            // Set status bar to solid dark brown
            window.statusBarColor = darkBrown

            // Explicitly set the decor view's system UI flags (for API < 30)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }

            // For API 30+ use the WindowInsetsController
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false // Force white icons
                isAppearanceLightNavigationBars = false
            }

            // Set navigation bar color
            window.navigationBarColor = CoffeeBrown.toArgb()

            // Make sure status bar has solid color, not translucent
            @Suppress("DEPRECATION")
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}