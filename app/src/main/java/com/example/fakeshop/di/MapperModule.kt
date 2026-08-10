package com.example.fakeshop.di

import com.example.fakeshop.data.mapper.ProductDtoToEntityMapper
import com.example.fakeshop.ui.theme.mapper.ProductEntityToUiMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Provides
    @Singleton
    fun productDtoToEntity(): ProductDtoToEntityMapper = ProductDtoToEntityMapper()

    @Provides
    @Singleton
    fun productEntityToUi(): ProductEntityToUiMapper = ProductEntityToUiMapper()
}