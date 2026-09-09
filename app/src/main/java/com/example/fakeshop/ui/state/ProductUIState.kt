package com.example.fakeshop.ui.state

import com.example.fakeshop.domain.result.AppError

sealed interface InitialLoadState {

    data object Idle : InitialLoadState
    data object Loading : InitialLoadState
    data object Success : InitialLoadState
    data class Error(val error: AppError) : InitialLoadState
}

sealed interface PaginationState {

    data object Idle : PaginationState
    data object Loading : PaginationState
    data object Success : PaginationState
    data class Error(val error: AppError) : PaginationState
}

sealed interface RefreshState {

    data object Idle : RefreshState
    data object Loading : RefreshState
    data object Success : RefreshState
    data class Error(val error: AppError) : RefreshState
}