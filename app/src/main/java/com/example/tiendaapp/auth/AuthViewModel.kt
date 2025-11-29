package com.example.tiendaapp.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import com.example.tiendaapp.ui.UiEvent

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = AuthRepository(application.applicationContext)

    private val _currentUserId = MutableStateFlow(repo.getCurrentUserId())
    val currentUserId: StateFlow<Long> = _currentUserId

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    fun register(name: String, email: String, password: CharArray, callback: (Result<Long>) -> Unit) {
        viewModelScope.launch {
            val res = repo.registerUser(name, email, password)
            if (res.isSuccess) _currentUserId.value = res.getOrDefault(-1L)
            if (res.isSuccess) {
                _events.emit(UiEvent("Registro exitoso", actionLabel = null, source = "auth"))
            } else {
                _events.emit(UiEvent("Error al registrar: ${res.exceptionOrNull()?.message}", actionLabel = null, source = "auth"))
            }
            callback(res)
        }
    }

    fun login(email: String, password: CharArray, callback: (Result<Long>) -> Unit) {
        viewModelScope.launch {
            val res = repo.login(email, password)
            if (res.isSuccess) _currentUserId.value = res.getOrDefault(-1L)
            if (res.isSuccess) {
                _events.emit(UiEvent("Sesión iniciada", actionLabel = null, source = "auth"))
            } else {
                _events.emit(UiEvent("Error al iniciar sesión: ${res.exceptionOrNull()?.message}", actionLabel = null, source = "auth"))
            }
            callback(res)
        }
    }
    
    fun refreshCurrentUser() {
        _currentUserId.value = repo.getCurrentUserId()
    }

    fun logout() {
        repo.logout()
        _currentUserId.value = -1L
    }
}
