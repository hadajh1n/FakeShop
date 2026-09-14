package com.example.fakeshop.domain.result

sealed interface AppError {

    data object Network : AppError
    data object Timeout : AppError
    data object Server : AppError
    data object Unauthorized : AppError
    data object Unknown : AppError
}