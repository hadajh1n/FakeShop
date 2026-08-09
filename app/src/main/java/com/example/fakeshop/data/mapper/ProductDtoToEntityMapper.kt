package com.example.fakeshop.data.mapper

import com.example.fakeshop.data.dataclass.ProductDTO
import com.example.fakeshop.data.room.ProductEntity

class ProductDtoToEntityMapper {

    fun fromDtoToEntity(dto: ProductDTO): ProductEntity =
        ProductEntity(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            category = dto.category,
            price = dto.price,
            discountPercentage = dto.discountPercentage,
            rating = dto.rating,
            stock = dto.stock,
            brand = dto.brand,
            thumbnail = dto.thumbnail,
            images = dto.images,
            tags = dto.tags,
            availabilityStatus = dto.availabilityStatus,
            isFavorite = false,
            cachedAt = System.currentTimeMillis(),
        )
}