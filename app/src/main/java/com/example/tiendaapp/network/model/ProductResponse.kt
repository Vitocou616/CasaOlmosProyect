package com.example.tiendaapp.network.model

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("imageUrl")
    val imageUrl: String?,
    
    @SerializedName("stock")
    val stock: Int,
    
    @SerializedName("createdAt")
    val createdAt: String?
)

data class ProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val stock: Int
)
