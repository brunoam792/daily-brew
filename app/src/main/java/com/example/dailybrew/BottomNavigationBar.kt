package com.example.dailybrew

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val items = listOf("home", "history", "logs", "limit")
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = screen) },
                label = { Text(text = screen.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) },
                selected = false,
                onClick = { navController.navigate(screen) }
            )
        }
    }
}
