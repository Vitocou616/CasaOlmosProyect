package com.example.tiendaapp.network.api

import com.example.tiendaapp.network.model.CartItemRequest
import com.example.tiendaapp.network.model.CartItemResponse
import retrofit2.Response
import retrofit2.http.*

interface CartApiService {
    
    @GET("api/cart/{userId}")
    suspend fun getCartItems(@Path("userId") userId: Long): Response<List<CartItemResponse>>
    
    @POST("api/cart/add")
    suspend fun addToCart(@Body item: CartItemRequest): Response<CartItemResponse>
    
    @DELETE("api/cart/item/{itemId}")
    suspend fun removeFromCart(@Path("itemId") itemId: Long): Response<Void>
    
    @DELETE("api/cart/clear/{userId}")
    suspend fun clearCart(@Path("userId") userId: Long): Response<Void>
}
