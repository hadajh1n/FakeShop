package com.example.fakeshop.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fakeshop.R
import com.example.fakeshop.ui.navigation.NavScreen

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == NavScreen.Products.route,
            onClick = { onNavigate(NavScreen.Products.route) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_products_screen),
                    contentDescription = "Products",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified,
                )
            },
            label = {
                Text("Products")
            }
        )

        NavigationBarItem(
            selected = currentRoute == NavScreen.Favorites.route,
            onClick = { onNavigate(NavScreen.Favorites.route) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_favorites_screen),
                    contentDescription = "Favorites",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified,
                )
            },
            label = {
                Text("Favorites")
            }
        )
    }
}