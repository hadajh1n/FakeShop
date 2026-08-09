package com.example.fakeshop.data.dataclass

data class ProductCache(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val thumbnail: String?,
    val images: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val availabilityStatus: String? = null,
    val isFavorite: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)