package com.example.fakeshop.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeshop.domain.repository.ProductsRepository
import com.example.fakeshop.domain.result.AppError
import com.example.fakeshop.domain.result.AppResult
import com.example.fakeshop.ui.theme.mapper.ProductDomainToUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ProductUIState {

    object Standard : ProductUIState()
    object Loading : ProductUIState()
    object Success : ProductUIState()
    data class Error(val value: AppError) : ProductUIState()
}

sealed class PaginationState {

    object Standard : PaginationState()
    object Loading : PaginationState()
}

sealed class RefreshState {

    object Standard : RefreshState()
    object Loading : RefreshState()
}

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductsRepository,
    private val mapperUI: ProductDomainToUiMapper,
) : ViewModel() {

    private var _uiState = MutableStateFlow<ProductUIState>(ProductUIState.Standard)
    val uiState: StateFlow<ProductUIState> = _uiState.asStateFlow()

    private var _paginationState = MutableStateFlow<PaginationState>(PaginationState.Standard)
    val paginationState: StateFlow<PaginationState> = _paginationState.asStateFlow()

    private var _refreshState = MutableStateFlow<RefreshState>(RefreshState.Standard)
    val refreshState: StateFlow<RefreshState> = _refreshState.asStateFlow()

    private var loadPageJob: Job? = null
    private var refreshJob: Job? = null

    val products = repository.observeProducts()
        .map { domains -> domains.map { mapperUI.fromDomainToUI(it) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    init {
        if (!repository.isCacheValid()) refreshData()
    }

    fun loadNextPage() {
        if (loadPageJob?.isActive == true) return
        if (repository.isLastPage()) return

        loadPageJob = viewModelScope.launch {

            _paginationState.value = PaginationState.Loading

            when (val result = repository.loadNextPage()) {
                is AppResult.Success -> _uiState.value = ProductUIState.Success
                is AppResult.Error   -> _uiState.value = ProductUIState.Error(result.error)
            }

            _paginationState.value = PaginationState.Standard
        }
    }

    fun refreshData() {
        loadPageJob?.cancel()

        refreshJob = viewModelScope.launch {

            _refreshState.value = RefreshState.Loading
            _uiState.value = ProductUIState.Loading

            when (val result = repository.refreshProducts()) {
                is AppResult.Success -> _uiState.value = ProductUIState.Success
                is AppResult.Error   -> _uiState.value = ProductUIState.Error(result.error)
            }

            _refreshState.value = RefreshState.Standard
        }
    }
}