package com.example.fakeshop.ui.theme.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fakeshop.R
import com.example.fakeshop.data.dataclass.ProductUI
import com.example.fakeshop.ui.theme.FakeShopTheme
import com.example.fakeshop.ui.theme.navigation.NavScreen
import com.example.fakeshop.ui.theme.viewModel.LoginUIState
import com.example.fakeshop.ui.theme.viewModel.LoginViewModel
import com.example.fakeshop.ui.theme.viewModel.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FakeShopTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                ) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        AppNavHost()
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var emailText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 15.dp,
                end = 15.dp,
                top = 250.dp,
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = emailText,
                onValueChange = { emailText = it },
                label = { Text(stringResource(R.string.tfEmailLabel)) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(25.dp),
                modifier = modifier
                    .fillMaxWidth()
            )

            TextField(
                value = passwordText,
                onValueChange = { passwordText = it },
                label = { Text(stringResource(R.string.tfPasswordLabel)) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(25.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
            )

            Button(
                onClick = { viewModel.login(onSuccess = onLoginSuccess) },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
            ) {
                if (uiState is LoginUIState.Loading) {
                    CircularProgressIndicator(modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.btnEntranceLabel))
                }
            }
        }
    }

}

@Composable
fun ProductsScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductsViewModel = hiltViewModel(),
    onProductClick: (ProductUI) -> Unit = {},
) {
    val products by viewModel.products.collectAsStateWithLifecycle(initialValue = emptyList())

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            items = products,
            key = { it.id },
        ) { product ->
            ProductItem(
                product = product,
                onClick = { onProductClick(product) },
            )
        }
    }
}

@Composable
fun ProductItem(
    product: ProductUI,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        AsyncImage(
            model = product.thumbnail,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(15.dp))
        )

        Text(text = product.title)

        Text(text = product.category)

        Text(text = "${product.price}")
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = NavScreen.Login.route,
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
    }
}