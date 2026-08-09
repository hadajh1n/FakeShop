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
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val tags: List<String> = emptyList(),
    val brand: String? = null,
    val sku: String? = null,
    val weight: Double? = null,
    val dimensions: DimensionsDTO? = null,
    val warrantyInformation: String? = null,
    val shippingInformation: String? = null,
    val availabilityStatus: String? = null,
    val reviews: List<ReviewDTO> = emptyList(),
    val returnPolicy: String? = null,
    val minimumOrderQuantity: Int? = null,
    val meta: MetaDTO? = null,
    val images: List<String> = emptyList(),
    val thumbnail: String? = null
)

data class DimensionsDTO(
    val width: Double,
    val height: Double,
    val depth: Double
)

data class ReviewDTO(
    val rating: Int,
    val comment: String,
    val date: String,
    val reviewerName: String,
    val reviewerEmail: String
)

data class MetaDTO(
    val createdAt: String,
    val updatedAt: String,
    val barcode: String? = null,
    val qrCode: String? = null
)