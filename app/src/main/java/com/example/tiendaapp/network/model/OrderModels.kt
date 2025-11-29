package com.example.tiendaapp.network.model

import com.google.gson.annotations.SerializedName

// Respuesta del servidor para una orden
data class OrderResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("total")
    val total: Double,
    @SerializedName("createdAt")
    val createdAt: Long,
    @SerializedName("items")
    val items: List<OrderItemResponse>
)

// Item de una orden desde el servidor
data class OrderItemResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("quantity")
    val quantity: Int
)

// Request para crear una orden
data class CreateOrderRequest(
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("total")
    val total: Double,
    @SerializedName("createdAt")
    val createdAt: Long,
    @SerializedName("items")
    val items: List<CreateOrderItemRequest>
)

// Item para crear orden
data class CreateOrderItemRequest(
    @SerializedName("productName")
    val productName: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("quantity")
    val quantity: Int
)
