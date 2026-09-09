package com.example.fakeshop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fakeshop.ui.screen.favorites.FavoritesScreen
import com.example.fakeshop.ui.screen.login.LoginScreen
import com.example.fakeshop.ui.screen.products.ProductsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavScreen.Login.route,
        modifier = modifier,
    ) {
        composable(NavScreen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(NavScreen.Products.route) {
                    popUpTo(NavScreen.Login.route) { inclusive = true }
                }
            })
        }

        composable(NavScreen.Products.route) {
            ProductsScreen()
        }

        composable(NavScreen.Favorites.route) {
            FavoritesScreen()
        }
    }
}