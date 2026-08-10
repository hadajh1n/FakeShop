package com.example.fakeshop.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeshop.data.repository.AppRepository
import com.example.fakeshop.ui.theme.mapper.ProductEntityToUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

sealed class ProductUIState {

    object Standard : ProductUIState()
    object Loading : ProductUIState()
    object Success : ProductUIState()
    data class Error(val message: String) : ProductUIState()
}

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: AppRepository,
    private val mapperUI: ProductEntityToUiMapper,
) : ViewModel() {

    private var _uiState = MutableStateFlow<ProductUIState>(ProductUIState.Standard)
    val uiState: StateFlow<ProductUIState> = _uiState.asStateFlow()

    init { requestProducts() }

    val products = repository.getAllProducts()
        .map { entities ->
            entities.map { entity ->
                mapperUI.fromEntityToUI(entity)
            }
        }

    fun requestProducts() {
        viewModelScope.launch {
            _uiState.value = ProductUIState.Loading

            val result = repository.loadProducts()

            if (result.isSuccess) {
                _uiState.value = ProductUIState.Success
            } else {
                result.exceptionOrNull()?.let { e ->
                    val errorMessage = when (e) {
                        is UnknownHostException -> "Нет подключения к интернету"
                        is HttpException -> "Ошибка сервера: ${e.code()}"
                        else -> e.localizedMessage ?: "Неизвестная ошибка"
                    }
                    _uiState.value = ProductUIState.Error(errorMessage)
                }
            }
        }
    }
}