package com.example.dailybrew

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavigation(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = "splashScreen", // Start at Splash Screen
        modifier = Modifier.padding(paddingValues)
    ) {
        composable("splashScreen") { SplashScreen(navController) }
        composable("home") { HomeScreen() } // Home Screen with menu bar
        composable("history") { HistoryScreen() }
        composable("logs") { LogsScreen(navController) }
        composable("limit") { LimitScreen() }
        composable("addDrink") { AddDrinkScreen(navController) }
    }
}
