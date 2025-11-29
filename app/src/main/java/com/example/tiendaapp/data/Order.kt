package com.example.tiendaapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val total: Double,
    val createdAt: Long
)
