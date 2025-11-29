package com.example.tiendaapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Int,
    val name: String,
    val price: Double,
    val quantity: Int
)
