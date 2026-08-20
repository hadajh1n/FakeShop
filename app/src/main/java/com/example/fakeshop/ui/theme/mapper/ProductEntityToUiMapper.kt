package com.example.fakeshop.ui.theme.mapper

import com.example.fakeshop.data.dataclass.ProductUI
import com.example.fakeshop.data.room.products.ProductEntity

class ProductEntityToUiMapper {

    fun fromEntityToUI(entity: ProductEntity): ProductUI =
        ProductUI(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            category = entity.category,
            price = entity.price,
            thumbnail = entity.thumbnail,
        )
}