package com.example.fakeshop.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.fakeshop.domain.repository.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class LoginUIState {

    object Standard : LoginUIState()
    object Loading : LoginUIState()
    object Success : LoginUIState()
    data class Error(val message: String) : LoginUIState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: ProductsRepository,
) : ViewModel() {

    private var _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Standard)
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    fun login(onSuccess: () -> Unit) = onSuccess()
}