package com.example.dailybrew

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.media3.common.Timeline
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dailybrew.ui.theme.CoffeeBrown
import com.example.dailybrew.ui.theme.CremeBg

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = CoffeeBrown,
        contentColor = CremeBg
    ) {
        val items = listOf(
            "home" to Icons.Filled.Home,
            "history" to Icons.Filled.History,
            "logs" to Icons.AutoMirrored.Filled.List,
            "limit" to Icons.Filled.Timeline
        )

        items.forEach { (screen, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = screen) },
                label = { Text(text = screen.replaceFirstChar { it.uppercase() }) },
                selected = currentRoute == screen,
                onClick = {
                    if (currentRoute != screen) {
                        navController.navigate(screen) {
                            // Pop up to the start destination of the graph to avoid building up a stack
                            popUpTo("home") {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CremeBg,
                    selectedTextColor = CremeBg,
                    unselectedIconColor = CremeBg.copy(alpha = 0.7f),
                    unselectedTextColor = CremeBg.copy(alpha = 0.7f),
                    indicatorColor = CoffeeBrown.copy(alpha = 0.3f)
                )
            )
        }
    }
}