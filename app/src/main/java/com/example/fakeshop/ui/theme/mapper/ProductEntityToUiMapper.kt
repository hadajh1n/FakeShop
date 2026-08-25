package com.example.fakeshop.ui.theme.mapper

import com.example.fakeshop.ui.theme.model.ProductUI
import com.example.fakeshop.domain.model.Product

class ProductDomainToUiMapper {

    fun fromDomainToUI(domain: Product): ProductUI =
        ProductUI(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            category = domain.category,
            price = domain.price,
            thumbnail = domain.thumbnail,
        )
}