package com.example.fakeshop.di

import com.example.fakeshop.data.mapper.ProductDtoToEntityMapper
import com.example.fakeshop.data.preferences.AppPreferences
import com.example.fakeshop.data.repository.AppRepository
import com.example.fakeshop.data.room.products.ProductDao
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
    fun provideRepository(
        productDao: ProductDao,
        preferences: AppPreferences,
        api: ProductApi,
        mapperDto: ProductDtoToEntityMapper,
    ): AppRepository = AppRepository(productDao, preferences, api, mapperDto)
}