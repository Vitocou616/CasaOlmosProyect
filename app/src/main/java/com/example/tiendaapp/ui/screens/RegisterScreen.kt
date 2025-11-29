package com.example.tiendaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.tiendaapp.network.RetrofitClient
import com.example.tiendaapp.network.model.RegisterRequest
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(onRegister: (String, String, String) -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errors by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun validate(): Boolean {
        val e = mutableListOf<String>()
        if (name.isBlank()) e.add("Nombre requerido")
        if (!email.contains("@")) e.add("Email inválido")
        if (password.length < 4) e.add("Password demasiado corto (mínimo 4 caracteres)")
        errors = e
        return e.isEmpty()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Registro", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name, 
            onValueChange = { name = it; errors = emptyList() }, 
            label = { Text("Nombre") }, 
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email, 
            onValueChange = { email = it; errors = emptyList() }, 
            label = { Text("Email") }, 
            modifier = Modifier.fillMaxWidth(), 
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password, 
            onValueChange = { password = it; errors = emptyList() }, 
            label = { Text("Password") }, 
            modifier = Modifier.fillMaxWidth(), 
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        errors.forEach { 
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (!validate()) return@Button
                    
                    isLoading = true
                    errors = emptyList()
                    
                    scope.launch {
                        try {
                            val response = RetrofitClient.authApi.register(
                                RegisterRequest(email.trim(), password, name.trim())
                            )
                            
                            if (response.isSuccessful && response.body() != null) {
                                val authResponse = response.body()!!
                                // Successful registration
                                onRegister(name.trim(), email.trim(), authResponse.id.toString())
                            } else {
                                errors = listOf("Error al registrar: Email puede estar en uso")
                            }
                        } catch (e: Exception) {
                            errors = listOf("Error de conexión: ${e.localizedMessage}")
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                }, 
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) { 
                Text("Registrar") 
            }
            Button(
                onClick = onBack, 
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) { 
                Text("Cancelar") 
            }
        }
    }
}
