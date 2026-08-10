package com.example.fakeshop.data.dataclass

data class ProductsResponse(
    val products: List<ProductDTO>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

data class ProductDTO(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val thumbnail: String,
)