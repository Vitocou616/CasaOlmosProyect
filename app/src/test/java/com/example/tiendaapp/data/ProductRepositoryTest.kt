package com.example.tiendaapp.data

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class ProductRepositoryTest {

    @Before
    fun setUp() {
        // Repository is an object, no setup needed
    }

    @Test
    fun `sampleProducts should contain 4 products`() {
        // Act
        val products = ProductRepository.sampleProducts

        // Assert
        assertEquals(4, products.size)
    }

    @Test
    fun `first product should be Cafetera with correct data`() {
        // Act
        val cafetera = ProductRepository.sampleProducts[0]

        // Assert
        assertEquals(1, cafetera.id)
        assertEquals("Cafetera", cafetera.name)
        assertEquals("Cafetera de goteo compacta", cafetera.description)
        assertEquals(49.99, cafetera.price, 0.01)
        assertNotNull(cafetera.imageUrl)
        assertTrue(cafetera.imageUrl!!.startsWith("https://"))
    }

    @Test
    fun `second product should be Auriculares with correct data`() {
        // Act
        val auriculares = ProductRepository.sampleProducts[1]

        // Assert
        assertEquals(2, auriculares.id)
        assertEquals("Auriculares", auriculares.name)
        assertEquals("In-ear con cancelación de ruido", auriculares.description)
        assertEquals(79.99, auriculares.price, 0.01)
        assertNotNull(auriculares.imageUrl)
    }

    @Test
    fun `third product should be Mochila with correct data`() {
        // Act
        val mochila = ProductRepository.sampleProducts[2]

        // Assert
        assertEquals(3, mochila.id)
        assertEquals("Mochila", mochila.name)
        assertEquals("Mochila urbana 20L", mochila.description)
        assertEquals(39.99, mochila.price, 0.01)
        assertNotNull(mochila.imageUrl)
    }

    @Test
    fun `fourth product should be Taza termica with correct data`() {
        // Act
        val taza = ProductRepository.sampleProducts[3]

        // Assert
        assertEquals(4, taza.id)
        assertEquals("Taza térmica", taza.name)
        assertEquals("Taza para café de viaje 350ml", taza.description)
        assertEquals(12.99, taza.price, 0.01)
        assertNotNull(taza.imageUrl)
    }

    @Test
    fun `all products should have unique ids`() {
        // Act
        val products = ProductRepository.sampleProducts
        val ids = products.map { it.id }.distinct()

        // Assert
        assertEquals(products.size, ids.size)
    }

    @Test
    fun `all products should have non-empty names`() {
        // Act
        val products = ProductRepository.sampleProducts

        // Assert
        products.forEach { product ->
            assertTrue("Product ${product.id} has empty name", product.name.isNotEmpty())
        }
    }

    @Test
    fun `all products should have non-empty descriptions`() {
        // Act
        val products = ProductRepository.sampleProducts

        // Assert
        products.forEach { product ->
            assertTrue("Product ${product.id} has empty description", product.description.isNotEmpty())
        }
    }

    @Test
    fun `all products should have positive prices`() {
        // Act
        val products = ProductRepository.sampleProducts

        // Assert
        products.forEach { product ->
            assertTrue("Product ${product.id} has non-positive price", product.price > 0.0)
        }
    }

    @Test
    fun `all products should have valid image URLs or imageRes`() {
        // Act
        val products = ProductRepository.sampleProducts

        // Assert
        products.forEach { product ->
            val hasImageUrl = product.imageUrl != null && product.imageUrl!!.isNotEmpty()
            val hasImageRes = product.imageRes != 0
            assertTrue(
                "Product ${product.id} has neither imageUrl nor imageRes",
                hasImageUrl || hasImageRes
            )
        }
    }

    @Test
    fun `products with imageUrl should have valid HTTPS URLs`() {
        // Act
        val products = ProductRepository.sampleProducts.filter { it.imageUrl != null }

        // Assert
        products.forEach { product ->
            assertTrue(
                "Product ${product.id} has invalid URL",
                product.imageUrl!!.startsWith("https://") || product.imageUrl!!.startsWith("http://")
            )
        }
    }

    @Test
    fun `repository should be accessible as singleton`() {
        // Act
        val repo1 = ProductRepository
        val repo2 = ProductRepository

        // Assert
        assertSame(repo1, repo2)
    }
}
