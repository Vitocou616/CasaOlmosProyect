package com.example.tiendaapp.network.api

import com.example.tiendaapp.network.model.CreateOrderRequest
import com.example.tiendaapp.network.model.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderApiService {
    @GET("api/orders/{userId}")
    suspend fun getOrdersByUser(@Path("userId") userId: Long): Response<List<OrderResponse>>

    @POST("api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<OrderResponse>

    @GET("api/orders/detail/{orderId}")
    suspend fun getOrderDetail(@Path("orderId") orderId: Long): Response<OrderResponse>
}
