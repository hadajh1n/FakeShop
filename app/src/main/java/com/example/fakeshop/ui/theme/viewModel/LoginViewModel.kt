package com.example.fakeshop.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeshop.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

sealed class LoginUIState {

    object Standard : LoginUIState()
    object Loading : LoginUIState()
    object Success : LoginUIState()
    data class Error(val message: String) : LoginUIState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AppRepository,
) : ViewModel() {

    private var _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Standard)
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = LoginUIState.Loading

            val result = repository.loadProducts()

            if (result.isSuccess) {
                _uiState.value = LoginUIState.Success
                onSuccess()
            } else {
                result.exceptionOrNull()?.let { e ->
                    val errorMessage = when (e) {
                        is UnknownHostException -> "Нет подключения к интернету"
                        is HttpException -> "Ошибка сервера: ${e.code()}"
                        else -> e.localizedMessage ?: "Неизвестная ошибка"
                    }
                    _uiState.value = LoginUIState.Error(errorMessage)
                }
            }
        }
    }
}