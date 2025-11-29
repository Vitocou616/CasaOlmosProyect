package com.example.tiendaapp.data

import org.junit.Test
import org.junit.Assert.*

class ProductTest {

    @Test
    fun `product creation with all fields should succeed`() {
        // Arrange & Act
        val product = Product(
            id = 1,
            name = "Cafetera",
            description = "Cafetera de goteo compacta",
            price = 49.99,
            imageRes = 0,
            imageUrl = "https://example.com/image.jpg"
        )

        // Assert
        assertEquals(1, product.id)
        assertEquals("Cafetera", product.name)
        assertEquals("Cafetera de goteo compacta", product.description)
        assertEquals(49.99, product.price, 0.01)
        assertEquals(0, product.imageRes)
        assertEquals("https://example.com/image.jpg", product.imageUrl)
    }

    @Test
    fun `product with default imageRes should be zero`() {
        // Arrange & Act
        val product = Product(
            id = 1,
            name = "Test Product",
            description = "Test Description",
            price = 10.0
        )

        // Assert
        assertEquals(0, product.imageRes)
        assertNull(product.imageUrl)
    }

    @Test
    fun `product with only imageRes should have null imageUrl`() {
        // Arrange & Act
        val product = Product(
            id = 1,
            name = "Test Product",
            description = "Test Description",
            price = 10.0,
            imageRes = 12345
        )

        // Assert
        assertEquals(12345, product.imageRes)
        assertNull(product.imageUrl)
    }

    @Test
    fun `product with only imageUrl should have zero imageRes`() {
        // Arrange & Act
        val product = Product(
            id = 1,
            name = "Test Product",
            description = "Test Description",
            price = 10.0,
            imageUrl = "https://example.com/image.jpg"
        )

        // Assert
        assertEquals(0, product.imageRes)
        assertEquals("https://example.com/image.jpg", product.imageUrl)
    }

    @Test
    fun `product price formatting should display correctly`() {
        // Arrange
        val product = Product(
            id = 1,
            name = "Test Product",
            description = "Test Description",
            price = 49.99
        )

        // Act
        val formattedPrice = String.format("$%.2f", product.price)

        // Assert
        assertEquals("$49.99", formattedPrice)
    }

    @Test
    fun `product with zero price should be valid`() {
        // Arrange & Act
        val product = Product(
            id = 1,
            name = "Free Product",
            description = "Free item",
            price = 0.0
        )

        // Assert
        assertEquals(0.0, product.price, 0.01)
    }

    @Test
    fun `product equality should work correctly`() {
        // Arrange
        val product1 = Product(
            id = 1,
            name = "Product",
            description = "Description",
            price = 10.0
        )
        val product2 = Product(
            id = 1,
            name = "Product",
            description = "Description",
            price = 10.0
        )

        // Assert
        assertEquals(product1, product2)
        assertEquals(product1.hashCode(), product2.hashCode())
    }

    @Test
    fun `products with different ids should not be equal`() {
        // Arrange
        val product1 = Product(
            id = 1,
            name = "Product",
            description = "Description",
            price = 10.0
        )
        val product2 = Product(
            id = 2,
            name = "Product",
            description = "Description",
            price = 10.0
        )

        // Assert
        assertNotEquals(product1, product2)
    }
}
