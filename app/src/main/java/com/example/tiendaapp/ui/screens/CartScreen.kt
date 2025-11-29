package com.example.tiendaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.tiendaapp.CartViewModel
import com.example.tiendaapp.data.CartItem

@Composable
fun CartScreen(viewModel: CartViewModel, onCheckout: () -> Unit) {
    val items = viewModel.items.collectAsState()
    val list = items.value
    val total = list.sumOf { it.price * it.quantity }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Carrito", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        if (list.isEmpty()) {
            Text("El carrito está vacío", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(list) { it: CartItem ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(it.name, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(onClick = { viewModel.changeQuantity(it.productId, -1) }) { Text("-") }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${it.quantity}")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(onClick = { viewModel.changeQuantity(it.productId, +1) }) { Text("+") }
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("$${it.price}", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { viewModel.remove(it) }) { Text("Eliminar") }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Total: $${String.format("%.2f", total)}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) { Text("Pagar") }
        }
    }
}
