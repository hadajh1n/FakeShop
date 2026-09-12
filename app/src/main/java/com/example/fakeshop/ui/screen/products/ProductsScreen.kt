package com.example.fakeshop.ui.screen.products

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fakeshop.R
import com.example.fakeshop.domain.result.AppError
import com.example.fakeshop.ui.model.ProductUI
import com.example.fakeshop.ui.state.InitialLoadState
import com.example.fakeshop.ui.state.PaginationState
import com.example.fakeshop.ui.state.RefreshState
import com.example.fakeshop.ui.toMessage
import com.example.fakeshop.ui.viewModel.ProductsViewModel
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ProductsScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductsViewModel = hiltViewModel(),
    onProductClick: (ProductUI) -> Unit = {},
) {
    val initialLoadState by viewModel.initialLoadState.collectAsStateWithLifecycle()
    val paginationState by viewModel.paginationState.collectAsStateWithLifecycle()
    val refreshState by viewModel.refreshState.collectAsStateWithLifecycle()
    val canLoadMore by viewModel.canLoadMore.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle(initialValue = emptyList())
    val gridState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

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
                    lastVisibleIndex >= totalItems - 2 &&
                    canLoadMore
                ) {
                    viewModel.loadNextPage(currentCount = products.size)
                }
            }
    }

    LaunchedEffect(Unit) {
        viewModel.paginationError.collect { error ->
            snackbarHostState.showSnackbar(
                message = error.toMessage(context)
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshError.collect { error ->
            snackbarHostState.showSnackbar(
                message = error.toMessage(context)
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (val state = initialLoadState) {
            is InitialLoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

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
                        text = when (state.error) {
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

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .padding(16.dp)
    )
}