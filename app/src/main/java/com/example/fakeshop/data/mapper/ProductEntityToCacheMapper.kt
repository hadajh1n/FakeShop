package com.example.fakeshop.data.mapper

import com.example.fakeshop.data.dataclass.ProductCache
import com.example.fakeshop.data.room.ProductEntity

class ProductEntityToCacheMapper {

    fun fromEntityToCache(entity: ProductEntity): ProductCache =
        ProductCache(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            category = entity.category,
            price = entity.price,
            discountPercentage = entity.discountPercentage,
            rating = entity.rating,
            stock = entity.stock,
            brand = entity.brand,
            thumbnail = entity.thumbnail,
            images = entity.images,
            tags = entity.tags,
            availabilityStatus = entity.availabilityStatus,
            isFavorite = entity.isFavorite,
            cachedAt = entity.cachedAt,
        )
}