package com.example.tiendaapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageRes: Int = 0,
    // URL para cargar imagen remota usando Coil (si está disponible, se usa en lugar de imageRes)
    val imageUrl: String? = null
)

