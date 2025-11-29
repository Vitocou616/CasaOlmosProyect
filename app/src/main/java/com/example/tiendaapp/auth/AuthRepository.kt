package com.example.tiendaapp.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.tiendaapp.data.AppDatabase
import com.example.tiendaapp.data.User
import com.example.tiendaapp.network.RetrofitClient
import com.example.tiendaapp.network.model.LoginRequest
import com.example.tiendaapp.network.model.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val userDao = db.userDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("tienda_prefs", Context.MODE_PRIVATE)
    private val authApi = RetrofitClient.authApi

    suspend fun registerUser(name: String, email: String, password: CharArray): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val hash = PasswordUtils.hashPassword(password)
            val user = User(name = name, email = email, passwordHash = hash)
            userDao.insert(user)
            // fetch created user id
            val created = userDao.getByEmail(email)
            if (created != null) {
                // set session
                prefs.edit().putLong("current_user_id", created.id).apply()
                Result.success(created.id)
            } else {
                Result.failure(Exception("No creado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: CharArray): Result<Long> = withContext(Dispatchers.IO) {
        try {
            // Intentar autenticar con el backend primero
            val passwordStr = String(password)
            android.util.Log.d("AuthRepository", "Intentando login con backend: $email")
            android.util.Log.d("AuthRepository", "Password length: ${passwordStr.length}, chars: [${passwordStr.toCharArray().joinToString(", ") { it.code.toString() }}]")
            
            try {
                val response = authApi.login(LoginRequest(email, passwordStr))
                
                android.util.Log.d("AuthRepository", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
                
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    val userId = authResponse.id
                    
                    android.util.Log.d("AuthRepository", "Login exitoso, userId: $userId, name: ${authResponse.name}")
                    
                    // Guardar sesión
                    prefs.edit().putLong("current_user_id", userId).apply()
                    
                    // Sincronizar con base de datos local (best effort, no crítico)
                    try {
                        var localUser = userDao.getByEmail(email)
                        if (localUser == null) {
                            // Crear usuario local si no existe
                            val hash = PasswordUtils.hashPassword(password)
                            val userName = authResponse.name ?: "Usuario"
                            val newUser = User(id = 0, name = userName, email = email, passwordHash = hash)
                            userDao.insert(newUser)
                            android.util.Log.d("AuthRepository", "Usuario local creado")
                        }
                    } catch (dbError: Exception) {
                        android.util.Log.w("AuthRepository", "No se pudo sincronizar localmente: ${dbError.message}")
                        // No crítico, continuar con el login exitoso
                    }
                    
                    return@withContext Result.success(userId)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Sin error body"
                    android.util.Log.d("AuthRepository", "Backend falló: $errorMsg")
                }
            } catch (netError: Exception) {
                android.util.Log.e("AuthRepository", "Error de red: ${netError.message}", netError)
            }
            
            // Si falla el backend, intentar login local como fallback
            android.util.Log.d("AuthRepository", "Intentando fallback local")
            val user = userDao.getByEmail(email)
            if (user != null && PasswordUtils.verifyPassword(password, user.passwordHash)) {
                android.util.Log.d("AuthRepository", "Login local exitoso")
                prefs.edit().putLong("current_user_id", user.id).apply()
                return@withContext Result.success(user.id)
            } else {
                android.util.Log.d("AuthRepository", "Login local falló - usuario: ${user != null}")
                return@withContext Result.failure(Exception("Usuario no encontrado o credenciales inválidas"))
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error general en login: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun logout() {
        prefs.edit().remove("current_user_id").apply()
    }

    fun getCurrentUserId(): Long = prefs.getLong("current_user_id", -1L)
}
