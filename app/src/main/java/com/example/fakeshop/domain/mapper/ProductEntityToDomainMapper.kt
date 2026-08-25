package com.example.fakeshop.domain.mapper

import com.example.fakeshop.data.room.products.ProductEntity
import com.example.fakeshop.domain.model.Product

class ProductEntityToDomainMapper {

    fun fromEntityToDomain(entity: ProductEntity): Product =
        Product(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            category = entity.category,
            price = entity.price,
            thumbnail = entity.thumbnail,
        )
}