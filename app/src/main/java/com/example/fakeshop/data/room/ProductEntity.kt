package com.example.fakeshop.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val thumbnail: String?,
    val images: String,
    val tags: String,
    val availabilityStatus: String?,
    val isFavorite: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)