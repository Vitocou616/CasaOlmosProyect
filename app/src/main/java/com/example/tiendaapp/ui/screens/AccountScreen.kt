package com.example.tiendaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tiendaapp.CartViewModel

@Composable
fun AccountScreen(
    userId: Long,
    userEmail: String?,
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val cartItems by cartViewModel.items.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Mi Cuenta", style = MaterialTheme.typography.titleLarge)
                Text("Usuario: ${userEmail ?: "#${userId}"}", style = MaterialTheme.typography.bodyMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onBack) { Text("Volver") }
                Button(onClick = onLogout) { Text("Cerrar sesión") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        // Purchases section (Orders history from backend)
        Text("Tus compras", style = MaterialTheme.typography.titleMedium)
        
        var orders by remember { mutableStateOf<List<com.example.tiendaapp.data.Order>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        
        val context = androidx.compose.ui.platform.LocalContext.current
        
        LaunchedEffect(userId) {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                isLoading = true
                errorMessage = null
                
                var loadedFromBackend = false
                
                // Intentar cargar desde el backend primero
                runCatching {
                    val response = com.example.tiendaapp.network.RetrofitClient.orderApi.getOrdersByUser(userId)
                    if (response.isSuccessful && response.body() != null) {
                        orders = response.body()!!.map { orderResp ->
                            com.example.tiendaapp.data.Order(
                                id = orderResp.id,
                                userId = orderResp.userId,
                                total = orderResp.total,
                                createdAt = orderResp.createdAt
                            )
                        }
                        loadedFromBackend = true
                    }
                }
                
                // Fallback a DB local si el backend falló
                if (!loadedFromBackend) {
                    runCatching {
                        val db = com.example.tiendaapp.data.AppDatabase.getInstance(context)
                        orders = db.orderDao().getOrdersByUser(userId)
                        errorMessage = "Mostrando órdenes locales"
                    }.onFailure {
                        errorMessage = "Error al cargar compras"
                    }
                }
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    isLoading = false
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(errorMessage!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        } else if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                Text("Aún no tienes compras registradas.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(orders) { order ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pedido #${order.id}", style = MaterialTheme.typography.bodyLarge)
                            Text("Fecha: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(order.createdAt))}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(String.format("$%.2f", order.total), style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
        }
    }
}
