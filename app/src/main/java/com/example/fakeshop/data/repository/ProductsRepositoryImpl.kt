package com.example.fakeshop.data.repository

import com.example.fakeshop.data.mapper.ProductDtoToEntityMapper
import com.example.fakeshop.data.preferences.AppPreferences
import com.example.fakeshop.data.room.products.ProductDao
import com.example.fakeshop.domain.mapper.ProductEntityToDomainMapper
import com.example.fakeshop.domain.model.Product
import com.example.fakeshop.domain.repository.ProductsRepository
import com.example.fakeshop.network.ProductApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductsRepositoryImpl(
    val productDao: ProductDao,
    val preferences: AppPreferences,
    val productApi: ProductApi,
    val mapperDtoEntity: ProductDtoToEntityMapper,
    val mapperEntityDomain: ProductEntityToDomainMapper,
) : ProductsRepository {

    companion object {
        private const val CACHE_VALID = 15 * 60 * 1000L
        private const val PRODUCTS_LIMIT = 30
    }

    override fun isCacheValid(): Boolean {
        val lastCache = preferences.getLastCacheUpdate()
        return System.currentTimeMillis() - lastCache < CACHE_VALID
    }

    override fun updateLastCacheTime() = preferences.updateLastCacheTime()

    override fun isLastPage(): Boolean = preferences.isLastPage()

    override fun observeProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities.map { mapperEntityDomain.fromEntityToDomain(it) }
        }
    }

    override suspend fun loadNextPage() {
        if (isLastPage()) return

        val skip = preferences.getCurrentSkip()
        val response = productApi.getProducts(PRODUCTS_LIMIT, skip)

        val entities = response.products.map { mapperDtoEntity.fromDtoToEntity(it) }
        productDao.insertProduct(entities)

        val newSkip = skip + response.products.size
        preferences.updateCurrentSkip(newSkip)

        if (newSkip >= response.total) preferences.updateLastPage()
    }

    override suspend fun refreshProducts() {
        productDao.clearAllProducts()
        preferences.resetPagination()
        loadNextPage()
        updateLastCacheTime()
    }
}