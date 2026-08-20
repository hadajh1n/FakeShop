package com.example.fakeshop.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fakeshop.data.room.products.ProductDao
import com.example.fakeshop.data.room.products.ProductEntity

@Database(
    entities = [
        ProductEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
}