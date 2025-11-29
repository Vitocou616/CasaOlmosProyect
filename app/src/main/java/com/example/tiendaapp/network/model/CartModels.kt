package com.example.tiendaapp.network.model

import com.google.gson.annotations.SerializedName

data class CartItemResponse(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("userId")
    val userId: Long,
    
    @SerializedName("productId")
    val productId: Long,
    
    @SerializedName("productName")
    val productName: String,
    
    @SerializedName("productPrice")
    val productPrice: Double,
    
    @SerializedName("productImage")
    val productImage: String?,
    
    @SerializedName("quantity")
    val quantity: Int
)

data class CartItemRequest(
    val userId: Long,
    val productId: Long,
    val productName: String,
    val productPrice: Double,
    val productImage: String?,
    val quantity: Int
)
