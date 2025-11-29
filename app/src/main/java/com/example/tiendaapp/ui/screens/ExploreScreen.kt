package com.example.tiendaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tiendaapp.data.Product
import com.example.tiendaapp.network.external.ExternalProduct
import com.example.tiendaapp.network.external.ExternalRetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(onBack: () -> Unit, onAddToCart: ((Product) -> Unit)? = null) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var products by remember { mutableStateOf<List<ExternalProduct>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val resp = ExternalRetrofitClient.api.getProducts()
                if (resp.isSuccessful && resp.body() != null) {
                    products = resp.body()!!.products
                } else {
                    error = "No se pudo cargar la API externa"
                }
            } catch (e: Exception) {
                error = "Error de conexión: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("🌍 Explorar Productos Externos") },
            navigationIcon = {
                TextButton(onClick = onBack) { Text("← Atrás") }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF6200EE),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )
    }) { inner ->
        Box(modifier = Modifier.fillMaxSize().padding(inner)) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                    CircularProgressIndicator(color = Color(0xFF6200EE)) 
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error!!, color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onBack) { Text("Volver") }
                    }
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(12.dp), 
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products) { p ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                Modifier.padding(12.dp), 
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = p.thumbnail,
                                    contentDescription = p.title,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        p.title, 
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 2
                                    )
                                    Text(
                                        p.description, 
                                        maxLines = 2, 
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        String.format("$%.2f", p.price), 
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                                
                                // Botón para agregar al carrito
                                if (onAddToCart != null) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Button(
                                            onClick = {
                                                // Convertir producto externo a producto local
                                                val localProduct = Product(
                                                    id = p.id,
                                                    name = p.title,
                                                    description = p.description,
                                                    price = p.price,
                                                    imageRes = 0,
                                                    imageUrl = p.thumbnail
                                                )
                                                onAddToCart(localProduct)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF6200EE)
                                            ),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Text("🛒 Agregar", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
