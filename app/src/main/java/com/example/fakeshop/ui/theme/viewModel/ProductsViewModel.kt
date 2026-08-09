package com.example.fakeshop.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import com.example.fakeshop.data.repository.AppRepository
import com.example.fakeshop.ui.theme.mapper.ProductEntityToUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: AppRepository,
    private val mapperUI: ProductEntityToUiMapper,
) : ViewModel() {

    val products = repository.getAllProducts()
        .map { entities ->
            entities.map { entity ->
                mapperUI.fromEntityToUI(entity)
            }
        }
}