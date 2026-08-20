package com.example.fakeshop.data.repository

import com.example.fakeshop.data.dataclass.ProductDTO
import com.example.fakeshop.data.dataclass.ProductsResponse
import com.example.fakeshop.data.mapper.ProductDtoToEntityMapper
import com.example.fakeshop.data.preferences.AppPreferences
import com.example.fakeshop.data.room.products.ProductDao
import com.example.fakeshop.network.ProductApi

class AppRepository(
    val productDao: ProductDao,
    val preferences: AppPreferences,
    val productApi: ProductApi,
    val mapperDto: ProductDtoToEntityMapper,
) {

    suspend fun loadProducts(limit: Int, skip: Int): ProductsResponse =
        productApi.getProducts(limit, skip)

    suspend fun setProduct(dto: List<ProductDTO>) {
        val entities = dto.map { mapperDto.fromDtoToEntity(it) }
        productDao.insertProduct(entities)
    }

    fun getAllProductsDatabase() = productDao.getAllProducts()
    suspend fun clearAllProductsDatabase() = productDao.clearAllProducts()


    fun getLastCache() = preferences.getLastCacheUpdate()
    fun updateLastCacheTime() = preferences.updateLastCacheTime()


    fun getCurrentSkip() = preferences.getCurrentSkip()
    fun setNewSkip(skip: Int) = preferences.updateCurrentSkip(skip)

    fun isLastPage() = preferences.isLastPage()
    fun updateLastPage() = preferences.updateLastPage()

    fun resetPagination() = preferences.resetPagination()
}