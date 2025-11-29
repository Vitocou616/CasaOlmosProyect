package com.example.tiendaapp.data

import com.example.tiendaapp.R
import com.example.tiendaapp.network.RetrofitClient
import com.example.tiendaapp.network.model.ProductRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ProductRepository {
    
    // Productos locales como fallback (si el servidor no está disponible)
    private val fallbackProducts = listOf(
        Product(
            1, "Cafetera", "Cafetera de goteo compacta", 49.99, R.drawable.ic_shop,
            "https://http2.mlstatic.com/D_NQ_NP_975534-MLC91531679389_092025-O.webp"
        ),
        Product(
            2, "Auriculares", "In-ear con cancelación de ruido", 79.99, 0,
            "https://m.media-amazon.com/images/I/51Fl+3ERtDL._AC_SL1200_.jpg"
        ),
        Product(
            3, "Mochila", "Mochila urbana 20L", 39.99, R.drawable.ic_shop,
            "https://http2.mlstatic.com/D_NQ_NP_638962-MLC81893450089_012025-O.webp"
        ),
        Product(
            4, "Taza térmica", "Taza para café de viaje 350ml", 12.99, R.drawable.ic_shop,
            "https://i.ebayimg.com/images/g/5AUAAOSwmCFjF58k/s-l1600.webp"
        )
    )
    
    // Obtener todos los productos desde el API
    suspend fun getAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.productApi.getAllProducts()
            if (response.isSuccessful && response.body() != null) {
                // Convertir ProductResponse a Product
                response.body()!!.map { productResponse ->
                    Product(
                        id = productResponse.id.toInt(),
                        name = productResponse.name,
                        description = productResponse.description,
                        price = productResponse.price,
                        imageRes = 0, // No usamos recursos locales
                        imageUrl = productResponse.imageUrl
                    )
                }
            } else {
                fallbackProducts // Usar fallback si falla
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fallbackProducts // Usar fallback si hay error de red
        }
    }
    
    // Buscar productos
    suspend fun searchProducts(query: String): List<Product> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.productApi.searchProducts(query)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.map { productResponse ->
                    Product(
                        id = productResponse.id.toInt(),
                        name = productResponse.name,
                        description = productResponse.description,
                        price = productResponse.price,
                        imageRes = 0,
                        imageUrl = productResponse.imageUrl
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    // Crear producto
    suspend fun createProduct(
        name: String,
        description: String,
        price: Double,
        imageUrl: String?,
        stock: Int
    ): Product? = withContext(Dispatchers.IO) {
        try {
            val request = ProductRequest(name, description, price, imageUrl, stock)
            val response = RetrofitClient.productApi.createProduct(request)
            if (response.isSuccessful && response.body() != null) {
                val productResponse = response.body()!!
                Product(
                    id = productResponse.id.toInt(),
                    name = productResponse.name,
                    description = productResponse.description,
                    price = productResponse.price,
                    imageRes = 0,
                    imageUrl = productResponse.imageUrl
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    // Mantener compatibilidad con código existente
    val sampleProducts: List<Product>
        get() = fallbackProducts
}

