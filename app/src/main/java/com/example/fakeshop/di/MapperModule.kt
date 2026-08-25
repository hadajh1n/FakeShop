package com.example.fakeshop.di

import com.example.fakeshop.data.mapper.ProductDtoToEntityMapper
import com.example.fakeshop.domain.mapper.ProductEntityToDomainMapper
import com.example.fakeshop.ui.theme.mapper.ProductDomainToUiMapper
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
    fun productEntityToDomain(): ProductEntityToDomainMapper = ProductEntityToDomainMapper()

    @Provides
    @Singleton
    fun productDomainToUi(): ProductDomainToUiMapper = ProductDomainToUiMapper()
}