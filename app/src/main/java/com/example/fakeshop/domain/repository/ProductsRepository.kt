package com.example.fakeshop.domain.repository

import com.example.fakeshop.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {

    fun isCacheValid(): Boolean
    fun updateLastCacheTime()
    fun isLastPage(): Boolean

    fun observeProducts(): Flow<List<Product>>

    suspend fun loadNextPage()
    suspend fun refreshProducts()
}