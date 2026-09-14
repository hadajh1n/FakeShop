package com.example.fakeshop.data.repository

import com.example.fakeshop.data.mapper.ProductDtoToEntityMapper
import com.example.fakeshop.data.preferences.AppPreferences
import com.example.fakeshop.data.room.products.ProductDao
import com.example.fakeshop.domain.mapper.ProductEntityToDomainMapper
import com.example.fakeshop.domain.model.Product
import com.example.fakeshop.domain.repository.ProductsRepository
import com.example.fakeshop.domain.result.AppError
import com.example.fakeshop.domain.result.AppResult
import com.example.fakeshop.network.ProductApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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
        private const val REFRESH_SKIP = 0
    }

    override fun isCacheValid(): Boolean {
        val lastCache = preferences.getLastCacheUpdate()
        return System.currentTimeMillis() - lastCache < CACHE_VALID
    }

    override suspend fun hasProducts(): Boolean = productDao.hasProducts()

    override fun updateLastCacheTime() = preferences.updateLastCacheTime()

    override fun isLastPage(): Boolean = preferences.isLastPage()

    override fun observeProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities.map { mapperEntityDomain.fromEntityToDomain(it) }
        }
    }

    override suspend fun reloadFromFirstPage(): AppResult<Unit> {

        return try {
            val response = productApi.getProducts(PRODUCTS_LIMIT, REFRESH_SKIP)

            productDao.clearAllProducts()
            val entities = response.products.map { mapperDtoEntity.fromDtoToEntity(it) }
            productDao.insertProduct(entities)

            val newSkip = REFRESH_SKIP + response.products.size
            preferences.updateCurrentSkip(newSkip)
            preferences.resetLastPage()

            if (newSkip >= response.total) preferences.updateLastPage()

            updateLastCacheTime()

            AppResult.Success(Unit)
        } catch (e: UnknownHostException) {
            AppResult.Error(AppError.Network)
        } catch (e: SocketTimeoutException) {
            AppResult.Error(AppError.Timeout)
        } catch (e: HttpException) {
            AppResult.Error(
                when (e.code()) {
                    401 -> AppError.Unauthorized
                    in 500..599 -> AppError.Server
                    else -> AppError.Unknown
                }
            )
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown)
        }
    }

    override suspend fun loadNextPage(): AppResult<Unit> {

        return try {
            val skip = preferences.getCurrentSkip()
            val response = productApi.getProducts(PRODUCTS_LIMIT, skip)

            val entities = response.products.map { mapperDtoEntity.fromDtoToEntity(it) }
            productDao.insertProduct(entities)

            val newSkip = skip + response.products.size
            preferences.updateCurrentSkip(newSkip)

            if (newSkip >= response.total) preferences.updateLastPage()

            AppResult.Success(Unit)
        } catch (e: UnknownHostException) {
            AppResult.Error(AppError.Network)
        } catch (e: SocketTimeoutException) {
            AppResult.Error(AppError.Timeout)
        } catch (e: HttpException) {
            AppResult.Error(
                when (e.code()) {
                    401 -> AppError.Unauthorized
                    in 500..599 -> AppError.Server
                    else -> AppError.Unknown
                }
            )
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown)
        }
    }
}