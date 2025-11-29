package com.example.tiendaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

@Composable
fun CheckoutScreen(onFinish: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var card by remember { mutableStateOf("") }
    var errors by remember { mutableStateOf(listOf<String>()) }

    fun validate(): Boolean {
        val e = mutableListOf<String>()
        if (name.isBlank()) e.add("Nombre requerido")
        if (address.isBlank()) e.add("Dirección requerida")
        if (card.length < 12) e.add("Tarjeta inválida")
        errors = e
        return e.isEmpty()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Checkout", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = card, onValueChange = { card = it }, label = { Text("Tarjeta") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        errors.forEach { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = {
            if (validate()) {
                onFinish("Compra de: $name\nDirección: $address")
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Finalizar") }
    }
}
