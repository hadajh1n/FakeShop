package com.example.fakeshop.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import com.example.fakeshop.data.repository.AppRepository
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
    private val repository: AppRepository,
) : ViewModel() {

    private var _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Standard)
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    fun login(onSuccess: () -> Unit) = onSuccess()
}