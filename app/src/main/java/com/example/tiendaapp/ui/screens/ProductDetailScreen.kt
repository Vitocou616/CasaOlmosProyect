package com.example.tiendaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tiendaapp.data.Product
import com.example.tiendaapp.R

@Composable
fun ProductDetailScreen(product: Product, onAdd: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Preferir drawable específico si existe, luego URL remota, sino carrito naranja
        AsyncImage(
            model = product.imageUrl?.takeIf { it.isNotBlank() } ?: if (product.imageRes != 0) product.imageRes else R.drawable.ic_shop,
            contentDescription = product.name,
            modifier = Modifier.fillMaxWidth().height(220.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(product.name, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(product.description, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Precio: ${String.format("$%.2f", product.price)}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onAdd) { Text("Agregar al carrito") }
            Button(onClick = onBack) { Text("Volver") }
        }
    }
}

