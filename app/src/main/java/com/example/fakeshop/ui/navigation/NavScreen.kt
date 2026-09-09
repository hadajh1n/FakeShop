package com.example.fakeshop.ui.navigation

sealed class NavScreen(val route: String) {

    object Login : NavScreen("login")
    object Products : NavScreen("products")
    object Favorites : NavScreen("favorites")
}