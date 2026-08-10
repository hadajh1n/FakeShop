package com.example.fakeshop.network

import com.example.fakeshop.data.dataclass.ProductsResponse
import retrofit2.http.GET

interface ProductApi {

    @GET("products")
    suspend fun getProducts(): ProductsResponse

//    @GET("products")
//    suspend fun getProducts(
//        @Query("limit") limit: Int = 30,
//        @Query("skip") skip: Int = 0,
//    ): ProductsResponse
}