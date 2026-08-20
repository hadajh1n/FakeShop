package com.example.fakeshop.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeshop.data.repository.AppRepository
import com.example.fakeshop.ui.theme.mapper.ProductEntityToUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

sealed class ProductUIState {

    object Standard : ProductUIState()
    object Loading : ProductUIState()
    object Success : ProductUIState()
    data class Error(val message: String) : ProductUIState()
}

sealed class PaginationState {

    object Standard : PaginationState()
    object Loading : PaginationState()
}

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: AppRepository,
    private val mapperUI: ProductEntityToUiMapper,
) : ViewModel() {

    companion object {
        private const val CACHE_VALID = 15 * 60 * 1000L
        private const val PRODUCTS_LIMIT = 30
    }

    private var _uiState = MutableStateFlow<ProductUIState>(ProductUIState.Standard)
    val uiState: StateFlow<ProductUIState> = _uiState.asStateFlow()

    private var _paginationState = MutableStateFlow<PaginationState>(PaginationState.Standard)
    val paginationState: StateFlow<PaginationState> = _paginationState.asStateFlow()

    private var loadPageJob: Job? = null

    val products = repository.getAllProductsDatabase()
        .map { entities ->
            entities.map { entity ->
                mapperUI.fromEntityToUI(entity)
            }
        }

    init {
        if (!isCacheValid()) {
            refreshData()
            updateCache()
        }
    }

    private fun isCacheValid(): Boolean {
        val lastCache = repository.getLastCache()
        return System.currentTimeMillis() - lastCache < CACHE_VALID
    }

    private fun updateCache() = repository.updateLastCacheTime()

    private suspend fun loadPage() {
        var skip = repository.getCurrentSkip()
        val response = repository.loadProducts(
            limit = PRODUCTS_LIMIT,
            skip = skip,
        )
        repository.setProduct(response.products)
        skip += response.products.size
        repository.setNewSkip(skip)
        if (skip >= response.total) repository.updateLastPage()
    }

    fun loadFirstPage() {
        if (loadPageJob?.isActive == true) return
        if (repository.isLastPage()) return

        _uiState.value = ProductUIState.Loading

        loadPageJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                loadPage()
                _uiState.value = ProductUIState.Success
            } catch (e: Exception) {
                _uiState.value = ProductUIState.Error("")
            }
        }
    }

    fun loadNextPage() {
        if (loadPageJob?.isActive == true) return
        if (repository.isLastPage()) return

        _paginationState.value = PaginationState.Loading

        loadPageJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                loadPage()
                _paginationState.value = PaginationState.Standard
            } catch (e: Exception) {
                _paginationState.value = PaginationState.Standard
            }
        }
    }

    fun refreshData() {
        repository.resetPagination()

        viewModelScope.launch(Dispatchers.IO) {

            repository.clearAllProductsDatabase()

            try {
                loadFirstPage()
            } catch (e: Exception) {

            }
        }
    }
}