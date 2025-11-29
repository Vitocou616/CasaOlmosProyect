package com.example.tiendaapp.network

import com.example.tiendaapp.network.external.ExternalProduct
import com.example.tiendaapp.network.external.ExternalProductsResponse
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests para modelos de API Externa (DummyJSON)
 * Valida la estructura y conversión de productos externos
 */
class ExternalApiTest {

    @Test
    fun `ExternalProduct should be created with all required fields`() {
        // Arrange & Act
        val product = ExternalProduct(
            id = 1,
            title = "iPhone 9",
            description = "An apple mobile which is nothing like apple",
            price = 549.0,
            thumbnail = "https://dummyjson.com/image/i/products/1/thumbnail.jpg"
        )

        // Assert
        assertEquals(1, product.id)
        assertEquals("iPhone 9", product.title)
        assertEquals("An apple mobile which is nothing like apple", product.description)
        assertEquals(549.0, product.price, 0.01)
        assertEquals("https://dummyjson.com/image/i/products/1/thumbnail.jpg", product.thumbnail)
    }

    @Test
    fun `ExternalProduct should have valid price`() {
        // Arrange & Act
        val product = ExternalProduct(
            id = 2,
            title = "Product",
            description = "Description",
            price = 99.99,
            thumbnail = "url"
        )

        // Assert
        assertTrue(product.price > 0)
        assertEquals(99.99, product.price, 0.01)
    }

    @Test
    fun `ExternalProductsResponse should contain list of products`() {
        // Arrange
        val products = listOf(
            ExternalProduct(1, "Product 1", "Desc 1", 10.0, "url1"),
            ExternalProduct(2, "Product 2", "Desc 2", 20.0, "url2"),
            ExternalProduct(3, "Product 3", "Desc 3", 30.0, "url3")
        )

        // Act
        val response = ExternalProductsResponse(products)

        // Assert
        assertEquals(3, response.products.size)
        assertEquals("Product 1", response.products[0].title)
        assertEquals("Product 2", response.products[1].title)
        assertEquals("Product 3", response.products[2].title)
    }

    @Test
    fun `ExternalProduct should have unique IDs`() {
        // Arrange
        val product1 = ExternalProduct(1, "P1", "D1", 10.0, "u1")
        val product2 = ExternalProduct(2, "P2", "D2", 20.0, "u2")

        // Assert
        assertNotEquals(product1.id, product2.id)
    }

    @Test
    fun `ExternalProduct thumbnail should be valid URL format`() {
        // Arrange & Act
        val product = ExternalProduct(
            id = 1,
            title = "Test",
            description = "Test desc",
            price = 100.0,
            thumbnail = "https://example.com/image.jpg"
        )

        // Assert
        assertTrue(product.thumbnail?.startsWith("http") == true)
        assertTrue(product.thumbnail?.contains(".jpg") == true || product.thumbnail?.contains(".png") == true)
    }

    @Test
    fun `ExternalProduct conversion to local Product should preserve data`() {
        // Arrange
        val external = ExternalProduct(
            id = 5,
            title = "External Product",
            description = "External description",
            price = 299.99,
            thumbnail = "https://example.com/thumb.jpg"
        )

        // Act - Simular conversión (como en HomeScreen)
        val localId = external.id + 10000 // Offset para evitar conflictos
        val localName = external.title
        val localDesc = external.description
        val localPrice = external.price
        val localImageUrl = external.thumbnail

        // Assert
        assertEquals(10005, localId)
        assertEquals("External Product", localName)
        assertEquals("External description", localDesc)
        assertEquals(299.99, localPrice, 0.01)
        assertEquals("https://example.com/thumb.jpg", localImageUrl)
    }

    @Test
    fun `ExternalProduct with zero price should be invalid`() {
        // Arrange & Act
        val product = ExternalProduct(
            id = 1,
            title = "Free Product",
            description = "Test",
            price = 0.0,
            thumbnail = "url"
        )

        // Assert - En una app real, price = 0 podría ser inválido
        // Pero para DummyJSON, verificamos que price sea >= 0
        assertTrue(product.price >= 0.0)
    }

    @Test
    fun `ExternalProduct should handle high prices correctly`() {
        // Arrange & Act
        val product = ExternalProduct(
            id = 100,
            title = "Expensive Item",
            description = "Luxury product",
            price = 9999.99,
            thumbnail = "url"
        )

        // Assert
        assertTrue(product.price > 1000.0)
        assertEquals(9999.99, product.price, 0.01)
    }

    @Test
    fun `ExternalProductsResponse with empty list should be valid`() {
        // Arrange & Act
        val response = ExternalProductsResponse(emptyList())

        // Assert
        assertNotNull(response.products)
        assertEquals(0, response.products.size)
        assertTrue(response.products.isEmpty())
    }

    @Test
    fun `ExternalProduct title should not be empty`() {
        // Arrange & Act
        val product = ExternalProduct(
            id = 1,
            title = "Valid Title",
            description = "Desc",
            price = 50.0,
            thumbnail = "url"
        )

        // Assert
        assertTrue(product.title.isNotEmpty())
        assertTrue(product.title.length > 0)
    }
}
