package com.example.fakeshop.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeshop.domain.repository.ProductsRepository
import com.example.fakeshop.domain.result.AppResult
import com.example.fakeshop.ui.theme.mapper.ProductDomainToUiMapper
import com.example.fakeshop.ui.theme.state.InitialLoadState
import com.example.fakeshop.ui.theme.state.PaginationState
import com.example.fakeshop.ui.theme.state.RefreshState
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

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductsRepository,
    private val mapperUI: ProductDomainToUiMapper,
) : ViewModel() {

    private var _initialLoadState = MutableStateFlow<InitialLoadState>(InitialLoadState.Idle)
    val initialLoadState: StateFlow<InitialLoadState> = _initialLoadState.asStateFlow()

    private var _paginationState = MutableStateFlow<PaginationState>(PaginationState.Idle)
    val paginationState: StateFlow<PaginationState> = _paginationState.asStateFlow()

    private var _refreshState = MutableStateFlow<RefreshState>(RefreshState.Idle)
    val refreshState: StateFlow<RefreshState> = _refreshState.asStateFlow()

    private var loadFirstPageJob: Job? = null
    private var loadNextPageJob: Job? = null
    private var refreshJob: Job? = null

    val products = repository.observeProducts()
        .map { domains -> domains.map { mapperUI.fromDomainToUI(it) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    init {
        if (!repository.isCacheValid()) loadFirstPage()
    }

    fun loadFirstPage() {
        if (loadFirstPageJob?.isActive == true) return
        if (repository.isLastPage()) return

        loadFirstPageJob = viewModelScope.launch {

            _initialLoadState.value = InitialLoadState.Loading

            when (val result = repository.reloadFromFirstPage()) {
                is AppResult.Success -> _initialLoadState.value =
                    InitialLoadState.Success
                is AppResult.Error   -> _initialLoadState.value =
                    InitialLoadState.Error(result.error)
            }

            _initialLoadState.value = InitialLoadState.Idle
        }
    }

    fun loadNextPage() {
        if (loadNextPageJob?.isActive == true) return
        if (repository.isLastPage()) return

        loadNextPageJob = viewModelScope.launch {

            _paginationState.value = PaginationState.Loading

            when (val result = repository.loadNextPage()) {
                is AppResult.Success -> _paginationState.value =
                    PaginationState.Success
                is AppResult.Error   -> _paginationState.value =
                    PaginationState.Error(result.error)
            }

            _paginationState.value = PaginationState.Idle
        }
    }

    fun refreshData() {
        loadNextPageJob?.cancel()

        refreshJob = viewModelScope.launch {

            _refreshState.value = RefreshState.Loading

            when (val result = repository.reloadFromFirstPage()) {
                is AppResult.Success -> _refreshState.value =
                    RefreshState.Success
                is AppResult.Error   -> _refreshState.value =
                    RefreshState.Error(result.error)
            }

            _refreshState.value = RefreshState.Idle
        }
    }
}