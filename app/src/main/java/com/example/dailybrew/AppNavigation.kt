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
        startDestination = "splashScreen",
        modifier = Modifier.padding(paddingValues)
    ) {
        composable("splashScreen") { SplashScreen(navController) }
        composable("home") { HomeScreen() }
        composable("history") { HistoryScreen() }
        composable("logs") { LogsScreen() }
        composable("limit") { LimitScreen() }
    }
}
