package com.example.tiendaapp.network.api

import com.example.tiendaapp.network.model.ProductRequest
import com.example.tiendaapp.network.model.ProductResponse
import retrofit2.Response
import retrofit2.http.*

interface ProductApiService {
    
    @GET("api/products")
    suspend fun getAllProducts(): Response<List<ProductResponse>>
    
    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Long): Response<ProductResponse>
    
    @GET("api/products/search")
    suspend fun searchProducts(@Query("query") query: String): Response<List<ProductResponse>>
    
    @POST("api/products")
    suspend fun createProduct(@Body product: ProductRequest): Response<ProductResponse>
    
    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Long,
        @Body product: ProductRequest
    ): Response<ProductResponse>
    
    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Response<Void>
}
