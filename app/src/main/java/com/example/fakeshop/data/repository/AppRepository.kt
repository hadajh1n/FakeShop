package com.example.fakeshop.data.repository

import com.example.fakeshop.data.dataclass.ProductDTO
import com.example.fakeshop.data.mapper.ProductDtoToEntityMapper
import com.example.fakeshop.data.mapper.ProductEntityToCacheMapper
import com.example.fakeshop.data.room.ProductDao
import com.example.fakeshop.network.ProductApi

class AppRepository(
    val productDao: ProductDao,
    val productApi: ProductApi,
    val mapperDto: ProductDtoToEntityMapper,
    val mapperCache: ProductEntityToCacheMapper,
) {

    suspend fun loadProducts(): Result<Unit> {
        return try {
            val response = productApi.getProducts()
            setProduct(response.products)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun setProduct(
        dto: List<ProductDTO>
    ) {
        dto.forEach { dto ->
            val entity = mapperDto.fromDtoToEntity(dto)
            productDao.insertProduct(entity)
        }
    }

    fun getAllProducts() = productDao.getAllProducts()
}