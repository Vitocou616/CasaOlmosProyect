package com.example.tiendaapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<OrderItem>)

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getOrdersByUser(userId: Long): List<Order>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrder(orderId: Long): List<OrderItem>
}
