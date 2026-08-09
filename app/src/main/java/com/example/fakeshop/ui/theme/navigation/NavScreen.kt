package com.example.fakeshop.ui.theme.navigation

sealed class NavScreen(val route: String) {

    object Login : NavScreen("login")
    object Products : NavScreen("products")
}