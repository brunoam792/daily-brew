package com.example.dailybrew

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(key1 = true) {
        delay(3000) //
        navController.navigate("home") {
            popUpTo("splashScreen") { inclusive = true }
        }
    }

    // UI da Splash Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Daily Brew",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
