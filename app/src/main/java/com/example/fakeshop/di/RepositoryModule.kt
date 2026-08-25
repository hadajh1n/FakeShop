package com.example.fakeshop.di

import com.example.fakeshop.data.mapper.ProductDtoToEntityMapper
import com.example.fakeshop.data.preferences.AppPreferences
import com.example.fakeshop.data.repository.ProductsRepositoryImpl
import com.example.fakeshop.data.room.products.ProductDao
import com.example.fakeshop.domain.mapper.ProductEntityToDomainMapper
import com.example.fakeshop.domain.repository.ProductsRepository
import com.example.fakeshop.network.ProductApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductsRepository(
        productDao: ProductDao,
        preferences: AppPreferences,
        api: ProductApi,
        mapperDtoEntity: ProductDtoToEntityMapper,
        mapperEntityDomain: ProductEntityToDomainMapper,
    ): ProductsRepository =

        ProductsRepositoryImpl(
            productDao,
            preferences,
            api,
            mapperDtoEntity,
            mapperEntityDomain,
        )
}