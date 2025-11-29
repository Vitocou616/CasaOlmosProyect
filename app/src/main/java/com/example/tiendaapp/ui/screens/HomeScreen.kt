package com.example.tiendaapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import com.example.tiendaapp.data.Product
import com.example.tiendaapp.data.ProductRepository
import com.example.tiendaapp.R
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    products: List<Product>,
    onProductClick: (Int) -> Unit,
    onOpenCart: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit,
    onExplore: () -> Unit,
    isLoggedIn: Boolean,
    onOpenAccount: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var apiProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var externalProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var recommendedProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Cargar productos desde la API al iniciar y mezclar con productos externos
    LaunchedEffect(Unit) {
        launch {
            try {
                // Cargar productos del backend
                apiProducts = ProductRepository.getAllProducts()
                
                // Cargar productos externos (API DummyJSON)
                try {
                    val externalResp = com.example.tiendaapp.network.external.ExternalRetrofitClient.api.getProducts()
                    if (externalResp.isSuccessful && externalResp.body() != null) {
                        // Convertir productos externos a formato local
                        val extProds = externalResp.body()!!.products.take(10).map { ext ->
                            Product(
                                id = ext.id + 10000, // Offset para evitar conflictos de ID
                                name = ext.title,
                                description = ext.description,
                                price = ext.price,
                                imageRes = 0,
                                imageUrl = ext.thumbnail
                            )
                        }
                        externalProducts = extProds
                        
                        // Mezclar productos locales y externos aleatoriamente para el carrusel
                        val combined = (apiProducts + extProds).shuffled()
                        recommendedProducts = combined.take(8) // Mostrar 8 productos aleatorios
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Si falla la API externa, solo usar productos locales
                    recommendedProducts = apiProducts.shuffled().take(8)
                }
                
                isLoading = false
            } catch (e: Exception) {
                e.printStackTrace()
                apiProducts = products // Fallback a productos locales
                recommendedProducts = products.shuffled().take(8)
                isLoading = false
            }
        }
    }
    
    // Usar productos de la API si están disponibles, sino usar los locales
    val displayProducts = if (apiProducts.isNotEmpty()) apiProducts else products
    val filtered = if (query.isBlank()) displayProducts 
                  else displayProducts.filter { 
                      it.name.contains(query, ignoreCase = true) || 
                      it.description.contains(query, ignoreCase = true) 
                  }

    var showAccountPopup by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        // Header: logo + search
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Logo placeholder (use ic_cart as the app logo)
            Image(
                painter = painterResource(id = R.drawable.ic_cart),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar por producto o marca...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
        }

        // Banner area - only the image (no overlay, no dots)
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 12.dp)) {
            Image(
                painter = painterResource(id = R.drawable.promo_banner),
                contentDescription = "Banner",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Section: Recomendados
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Recomendados para ti", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = onExplore,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                modifier = Modifier.height(32.dp)
            ) {
                Text("🌍 Explorar API Externa", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal carousel of recommended products (mezcla de locales y externos)
        LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(recommendedProducts) { p ->
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .clickable { onProductClick(p.id) }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        AsyncImage(
                            model = p.imageUrl?.takeIf { it.isNotBlank() } ?: if (p.imageRes != 0) p.imageRes else R.drawable.ic_shop,
                            contentDescription = p.name,
                            modifier = Modifier
                                .height(100.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(p.name, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(String.format("$%.2f", p.price), style = MaterialTheme.typography.titleSmall, color = Color(0xFF2E7D32))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Rest of content: full vertical list
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(filtered) { p ->
                Card(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = p.imageUrl?.takeIf { it.isNotBlank() } ?: if (p.imageRes != 0) p.imageRes else R.drawable.ic_shop,
                            contentDescription = p.name,
                            modifier = Modifier.size(80.dp).padding(end = 12.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(p.name, style = MaterialTheme.typography.titleMedium)
                            Text(p.description, style = MaterialTheme.typography.bodySmall)
                            Text(String.format("$%.2f", p.price), style = MaterialTheme.typography.bodyLarge)
                        }

                        Column {
                            Button(onClick = { onProductClick(p.id) }) { Text("Detalles") }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { onAddToCart(p) }) { Text("Agregar") }
                        }
                    }
                }
            }
        }

        // Bottom navigation bar
        Surface(shadowElevation = 8.dp) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color.White), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { /* Already on Inicio */ }) {
                    Text("🏠", style = MaterialTheme.typography.titleLarge)
                    Text("Inicio", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { /* TODO: Navigate to Categories */ }) {
                    Text("📂", style = MaterialTheme.typography.titleLarge)
                    Text("Categorias", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onOpenCart() }) {
                    Text("🛒", style = MaterialTheme.typography.titleLarge)
                    Text("Carrito", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { /* TODO: Navigate to Black Friday */ }) {
                    Text("🔥", style = MaterialTheme.typography.titleLarge)
                    Text("Black Friday", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {
                    if (isLoggedIn) {
                        onOpenAccount()
                    } else {
                        showAccountPopup = true
                    }
                }) {
                    Text("👤", style = MaterialTheme.typography.titleLarge)
                    Text("Cuenta", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Popup de cuenta: elegir Iniciar Sesión o Registrarse
        if (showAccountPopup) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showAccountPopup = false },
                title = { Text("Tu cuenta") },
                text = { Text("Inicia sesión para ver tus compras o crea una cuenta nueva.") },
                confirmButton = {
                    Button(onClick = {
                        showAccountPopup = false
                        onLogin()
                    }) { Text("Iniciar Sesión") }
                },
                dismissButton = {
                    Button(onClick = {
                        showAccountPopup = false
                        onRegister()
                    }) { Text("Registrarse") }
                }
            )
        }
    }
}

