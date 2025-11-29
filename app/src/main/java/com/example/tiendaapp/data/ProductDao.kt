package com.example.tiendaapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAll(): Flow<List<CartItem>>

    @Insert
    suspend fun insert(item: CartItem)

    @Delete
    suspend fun delete(item: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clear()

    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    suspend fun getByProductId(productId: Int): CartItem?

    @androidx.room.Update
    suspend fun update(item: CartItem)
}
