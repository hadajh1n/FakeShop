package com.example.fakeshop.ui.theme.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import com.example.fakeshop.R
import com.example.fakeshop.domain.result.AppError
import com.example.fakeshop.ui.theme.model.ProductUI
import com.example.fakeshop.ui.theme.FakeShopTheme
import com.example.fakeshop.ui.theme.navigation.NavScreen
import com.example.fakeshop.ui.theme.state.InitialLoadState
import com.example.fakeshop.ui.theme.state.PaginationState
import com.example.fakeshop.ui.theme.state.RefreshState
import com.example.fakeshop.ui.theme.viewModel.LoginUIState
import com.example.fakeshop.ui.theme.viewModel.LoginViewModel
import com.example.fakeshop.ui.theme.viewModel.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged

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
    val initialLoadState by viewModel.initialLoadState.collectAsStateWithLifecycle()
    val paginationState by viewModel.paginationState.collectAsStateWithLifecycle()
    val refreshState by viewModel.refreshState.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle(initialValue = emptyList())
    val gridState = rememberLazyGridState()

    LaunchedEffect(gridState) {
        snapshotFlow {
            val lastVisibleIndex =
                gridState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index

            val totalItems =
                gridState.layoutInfo.totalItemsCount

            lastVisibleIndex to totalItems
        }
            .distinctUntilChanged()
            .collect { (lastVisibleIndex, totalItems) ->

                if (
                    lastVisibleIndex != null &&
                    totalItems > 0 &&
                    lastVisibleIndex >= totalItems - 2
                ) {
                    viewModel.loadNextPage()
                }
            }
    }

    when (val state = initialLoadState) {
        is InitialLoadState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        is InitialLoadState.Success,
        is InitialLoadState.Idle -> {
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(stringResource(R.string.emptyList))
                }
            } else {
                PullToRefreshBox(
                    isRefreshing = refreshState is RefreshState.Loading,
                    onRefresh = { viewModel.refreshData() }
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(12.dp),
                        modifier = modifier.fillMaxWidth(),
                        state = gridState,
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

                        if (paginationState is PaginationState.Loading) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }

            }
        }

        is InitialLoadState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = when (state) {
                        AppError.Network -> stringResource(R.string.errorUnknownHostException)
                        AppError.Unauthorized -> stringResource(R.string.errorUnauthorized)
                        AppError.Server -> stringResource(R.string.errorServer)
                        else -> stringResource(R.string.errorUnknown)
                    }
                )
            }
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
        SubcomposeAsyncImage(
            model = product.thumbnail,
            contentDescription = product.title,
            modifier = Modifier
                .size(150.dp)
                .align(alignment = Alignment.CenterHorizontally),
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.errorImageLoad),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(alignment = Alignment.Center),
                        textAlign = TextAlign.Center,
                    )
                }
            }
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