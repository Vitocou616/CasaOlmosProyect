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
import androidx.compose.ui.platform.LocalContext
import com.example.tiendaapp.auth.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLogin: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Iniciar sesión", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email, 
            onValueChange = { email = it; error = null }, 
            label = { Text("Email") }, 
            modifier = Modifier.fillMaxWidth(), 
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password, 
            onValueChange = { password = it; error = null }, 
            label = { Text("Password") }, 
            modifier = Modifier.fillMaxWidth(), 
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        error?.let { 
            Text(it, color = MaterialTheme.colorScheme.error) 
            Spacer(modifier = Modifier.height(12.dp))
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        error = "Email y password requeridos"
                        return@Button
                    }
                    
                    isLoading = true
                    error = null
                    
                    scope.launch {
                        try {
                            val result = authRepository.login(email.trim(), password.toCharArray())
                            
                            if (result.isSuccess) {
                                // Successful login
                                onLogin()
                            } else {
                                error = result.exceptionOrNull()?.message ?: "Error de login"
                            }
                        } catch (e: Exception) {
                            error = "Error de conexión: ${e.localizedMessage}"
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                }, 
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) { 
                Text("Entrar") 
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
