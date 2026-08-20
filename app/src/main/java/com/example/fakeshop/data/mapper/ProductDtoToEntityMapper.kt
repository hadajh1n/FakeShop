package com.example.fakeshop.data.mapper

import com.example.fakeshop.data.dataclass.ProductDTO
import com.example.fakeshop.data.room.products.ProductEntity

class ProductDtoToEntityMapper {

    fun fromDtoToEntity(dto: ProductDTO): ProductEntity =
        ProductEntity(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            category = dto.category,
            price = dto.price,
            thumbnail = dto.thumbnail,
        )
}