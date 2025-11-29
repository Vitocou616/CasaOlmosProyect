package com.example.tiendaapp.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class OrderDaoTest {
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndQueryOrders() {
        val orderDao = db.orderDao()
        val oid = runBlocking {
            orderDao.insertOrder(Order(userId = 7, total = 123.45, createdAt = System.currentTimeMillis()))
        }
        assertTrue(oid > 0)

        val items = listOf(
            OrderItem(orderId = oid, productName = "Producto A", price = 10.0, quantity = 2),
            OrderItem(orderId = oid, productName = "Producto B", price = 5.5, quantity = 1)
        )
        runBlocking { orderDao.insertItems(items) }

        val fetched = runBlocking { orderDao.getOrdersByUser(7) }
        assertEquals(1, fetched.size)
        assertEquals(123.45, fetched.first().total, 0.001)

        val fetchedItems = runBlocking { orderDao.getItemsForOrder(oid) }
        assertEquals(2, fetchedItems.size)
        assertEquals("Producto A", fetchedItems[0].productName)
    }
}
