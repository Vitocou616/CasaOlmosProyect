package com.example.tiendaapp.data

import org.junit.Test
import org.junit.Assert.*

class CartItemTest {

    @Test
    fun `cartItem creation with all fields should succeed`() {
        // Arrange & Act
        val cartItem = CartItem(
            id = 1,
            productId = 100,
            name = "Test Product",
            price = 49.99,
            quantity = 5
        )

        // Assert
        assertEquals(1, cartItem.id)
        assertEquals(100, cartItem.productId)
        assertEquals("Test Product", cartItem.name)
        assertEquals(49.99, cartItem.price, 0.01)
        assertEquals(5, cartItem.quantity)
    }

    @Test
    fun `cartItem with auto-generated id defaults to zero`() {
        // Arrange & Act
        val cartItem = CartItem(
            productId = 100,
            name = "Test Product",
            price = 49.99,
            quantity = 5
        )

        // Assert
        assertEquals(0, cartItem.id)
        assertEquals(100, cartItem.productId)
        assertEquals(5, cartItem.quantity)
    }

    @Test
    fun `cartItem with quantity one should be valid`() {
        // Arrange & Act
        val cartItem = CartItem(
            id = 1,
            productId = 100,
            name = "Test Product",
            price = 49.99,
            quantity = 1
        )

        // Assert
        assertEquals(1, cartItem.quantity)
    }

    @Test
    fun `cartItem with large quantity should be valid`() {
        // Arrange & Act
        val cartItem = CartItem(
            id = 1,
            productId = 100,
            name = "Test Product",
            price = 49.99,
            quantity = 999
        )

        // Assert
        assertEquals(999, cartItem.quantity)
    }

    @Test
    fun `cartItem price formatting should display correctly`() {
        // Arrange
        val cartItem = CartItem(
            id = 1,
            productId = 100,
            name = "Test Product",
            price = 49.99,
            quantity = 1
        )

        // Act
        val formattedPrice = String.format("$%.2f", cartItem.price)

        // Assert
        assertEquals("$49.99", formattedPrice)
    }

    @Test
    fun `cartItem total price calculation should be correct`() {
        // Arrange
        val cartItem = CartItem(
            id = 1,
            productId = 100,
            name = "Test Product",
            price = 49.99,
            quantity = 3
        )

        // Act
        val total = cartItem.price * cartItem.quantity

        // Assert
        assertEquals(149.97, total, 0.01)
    }

    @Test
    fun `cartItem equality should work correctly`() {
        // Arrange
        val cartItem1 = CartItem(
            id = 1,
            productId = 100,
            name = "Test Product",
            price = 49.99,
            quantity = 5
        )
        val cartItem2 = CartItem(
            id = 1,
            productId = 100,
            name = "Test Product",
            price = 49.99,
            quantity = 5
        )

        // Assert
        assertEquals(cartItem1, cartItem2)
        assertEquals(cartItem1.hashCode(), cartItem2.hashCode())
    }

    @Test
    fun `cartItems with different ids should not be equal`() {
        // Arrange
        val cartItem1 = CartItem(
            id = 1,
            productId = 100,
            name = "Test Product",
            price = 49.99,
            quantity = 5
        )
        val cartItem2 = CartItem(
            id = 2,
            productId = 100,
            name = "Test Product",
            price = 49.99,
            quantity = 5
        )

        // Assert
        assertNotEquals(cartItem1, cartItem2)
    }

    @Test
    fun `cartItems with different productIds should not be equal`() {
        // Arrange
        val cartItem1 = CartItem(
            id = 1,
            productId = 100,
            name = "Test Product",
            price = 49.99,
            quantity = 5
        )
        val cartItem2 = CartItem(
            id = 1,
            productId = 200,
            name = "Test Product",
            price = 49.99,
            quantity = 5
        )

        // Assert
        assertNotEquals(cartItem1, cartItem2)
    }

    @Test
    fun `cartItems with different quantities should not be equal`() {
        // Arrange
        val cartItem1 = CartItem(
            id = 1,
            productId = 100,
            name = "Test Product",
            price = 49.99,
            quantity = 5
        )
        val cartItem2 = CartItem(
            id = 1,
            productId = 100,
            name = "Test Product",
            price = 49.99,
            quantity = 10
        )

        // Assert
        assertNotEquals(cartItem1, cartItem2)
    }
}

