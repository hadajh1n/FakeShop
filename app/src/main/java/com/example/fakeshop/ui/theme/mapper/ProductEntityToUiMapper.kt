package com.example.fakeshop.ui.theme.mapper

import com.example.fakeshop.data.dataclass.ProductUI
import com.example.fakeshop.data.room.ProductEntity

class ProductEntityToUiMapper {

    fun fromEntityToUI(entity: ProductEntity): ProductUI =
        ProductUI(
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
        )
}