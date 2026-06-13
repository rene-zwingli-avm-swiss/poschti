package swiss.avm.poschti.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import swiss.avm.poschti.ui.navigation.PoschtiDestination
import swiss.avm.poschti.ui.products.ProductsScreen
import swiss.avm.poschti.ui.recipes.RecipesScreen
import swiss.avm.poschti.ui.shopping.ShoppingScreen
import swiss.avm.poschti.ui.week.WeekScreen

@Composable
fun PoschtiApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                PoschtiDestination.entries.forEach { dest ->
                    val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = PoschtiDestination.startDestination.route,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            composable(PoschtiDestination.PRODUCTS.route) { ProductsScreen() }
            composable(PoschtiDestination.RECIPES.route) { RecipesScreen() }
            composable(PoschtiDestination.WEEK.route) {
                WeekScreen(
                    onListGenerated = {
                        navController.navigate(PoschtiDestination.LIST.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            composable(PoschtiDestination.LIST.route) {
                ShoppingScreen()
            }
        }
    }
}
