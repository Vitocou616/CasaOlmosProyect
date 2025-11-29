package com.example.tiendaapp.network.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class AuthResponse(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("message")
    val message: String
)

data class ErrorResponse(
    @SerializedName("error")
    val error: String
)
