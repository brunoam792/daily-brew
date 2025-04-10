package com.example.dailybrew

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.rememberNavController
import com.example.dailybrew.ui.theme.DailyBrewTheme
import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.dailybrew.ui.theme.CoffeeBrown

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Use the splash screen API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val splashScreen = installSplashScreen()
            splashScreen.setKeepOnScreenCondition { true }

            // Delay the removal of splash screen
            Handler(Looper.getMainLooper()).postDelayed({
                splashScreen.setKeepOnScreenCondition { false }
            }, 3000)
        }

        super.onCreate(savedInstanceState)
        setContent {
            DailyBrewTheme {
                DailyBrewApp()
            }
        }
    }
}

@Composable
fun DailyBrewApp() {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)
        .value?.destination?.route
    val showBottomBar = currentDestination != "splashScreen"

    // Fix status bar first, before Scaffold
    FixStatusBar(isLightStatusBar = false, statusBarColor = CoffeeBrown)

    Scaffold(
        bottomBar = { if (showBottomBar) BottomNavigationBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize()
            )
            AppNavigation(navController, paddingValues)
        }
    }
}

@Composable
fun FixStatusBar(
    isLightStatusBar: Boolean = false,
    statusBarColor: Color = CoffeeBrown
) {
    // Get window information
    val view = LocalView.current
    if (!view.isInEditMode) {
        val context = LocalContext.current
        val window = (context as? Activity)?.window

        LaunchedEffect(isLightStatusBar, statusBarColor) {
            window?.let {
                // Set status bar color
                it.statusBarColor = statusBarColor.toArgb()

                // Force black status bar on older versions
                if (Build.VERSION.SDK_INT < 30) {
                    @Suppress("DEPRECATION")
                    it.decorView.systemUiVisibility = if (isLightStatusBar) {
                        it.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    } else {
                        it.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                    }
                } else {
                    // Use the new API for Android 11+
                    WindowCompat.getInsetsController(it, view).apply {
                        isAppearanceLightStatusBars = isLightStatusBar
                        isAppearanceLightNavigationBars = isLightStatusBar
                    }
                }

                // Make sure we have solid status bar, not translucent
                @Suppress("DEPRECATION")
                it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
        }
    }
}


