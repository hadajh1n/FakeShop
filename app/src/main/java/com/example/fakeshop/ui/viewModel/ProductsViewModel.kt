package com.example.fakeshop.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeshop.domain.repository.ProductsRepository
import com.example.fakeshop.domain.result.AppError
import com.example.fakeshop.domain.result.AppResult
import com.example.fakeshop.ui.mapper.ProductDomainToUiMapper
import com.example.fakeshop.ui.state.InitialLoadState
import com.example.fakeshop.ui.state.PaginationState
import com.example.fakeshop.ui.state.RefreshState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _canLoadMore = MutableStateFlow(true)
    val canLoadMore: StateFlow<Boolean> = _canLoadMore.asStateFlow()

    private val _paginationError = MutableSharedFlow<AppError>()
    val paginationError = _paginationError.asSharedFlow()

    private val _refreshError = MutableSharedFlow<AppError>()
    val refreshError = _refreshError.asSharedFlow()

    private var loadFirstPageJob: Job? = null
    private var loadNextPageJob: Job? = null
    private var refreshJob: Job? = null

    private var lastRequestedCount = -1

    val products = repository.observeProducts()
        .map { domains -> domains.map { mapperUI.fromDomainToUI(it) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    init {
        viewModelScope.launch {
            if (!repository.hasProducts()) loadFirstPage()
            else if (!repository.isCacheValid()) refreshData()
        }
    }

    private fun loadFirstPage() {
        if (loadFirstPageJob?.isActive == true) return
        if (repository.isLastPage()) return

        loadFirstPageJob = viewModelScope.launch {
            _initialLoadState.value = InitialLoadState.Loading

            when (val result = repository.reloadFromFirstPage()) {
                is AppResult.Success -> _initialLoadState.value =
                    InitialLoadState.Idle
                is AppResult.Error -> _initialLoadState.value =
                    InitialLoadState.Error(result.error)
            }
        }
    }

    fun loadNextPage(currentCount: Int) {
        if (!_canLoadMore.value || loadNextPageJob?.isActive == true) return
        if (repository.isLastPage()) {
            _canLoadMore.value = false
            return
        }

        if (currentCount <= lastRequestedCount) return

        _canLoadMore.value = false
        lastRequestedCount = currentCount

        loadNextPageJob = viewModelScope.launch {
            _paginationState.value = PaginationState.Loading

            when (val result = repository.loadNextPage()) {
                is AppResult.Success -> {
                    _paginationState.value = PaginationState.Idle
                    _canLoadMore.value = true
                }
                is AppResult.Error -> {
                    _paginationError.emit(result.error)
                    _paginationState.value = PaginationState.Idle
                    lastRequestedCount = -1
                    _canLoadMore.value = true
                }
            }
        }
    }

    fun refreshData() {
        loadNextPageJob?.cancel()
        lastRequestedCount = -1
        _canLoadMore.value = true
        _paginationState.value = PaginationState.Idle

        refreshJob = viewModelScope.launch {
            _refreshState.value = RefreshState.Loading

            when (val result = repository.reloadFromFirstPage()) {
                is AppResult.Success -> {
                    _refreshState.value = RefreshState.Idle
                }
                is AppResult.Error -> {
                    _refreshError.emit(result.error)
                    _refreshState.value = RefreshState.Idle
                }
            }
        }
    }
}