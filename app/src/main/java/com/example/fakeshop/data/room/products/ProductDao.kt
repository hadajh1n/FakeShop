package com.example.fakeshop.data.room.products

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT EXISTS(SELECT 1 FROM products LIMIT 1)")
    suspend fun hasProducts(): Boolean

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(entities: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun clearAllProducts()
}