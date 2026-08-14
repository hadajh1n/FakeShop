package com.example.fakeshop.network

import com.example.fakeshop.data.dataclass.ProductsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApi {

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
    ): ProductsResponse
}