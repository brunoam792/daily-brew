package com.example.dailybrew

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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

    // Collect navigation state safely
    val currentDestination = navController.currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)
        .value?.destination?.route

    val showBottomBar = currentDestination != "splashScreen" // Hide menu on Splash

    Scaffold(
        bottomBar = { if (showBottomBar) BottomNavigationBar(navController) }
    ) { paddingValues ->
        // Apply background to all screens inside the scaffold
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Set the background image
            Image(
                painter = painterResource(id = R.drawable.background), // Set your image here
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize()
            )

            // Your app's content here
            AppNavigation(navController, paddingValues)
        }
    }
}



