package com.example.tiendaapp.network

import com.example.tiendaapp.network.api.AuthApiService
import com.example.tiendaapp.network.api.CartApiService
import com.example.tiendaapp.network.api.OrderApiService
import com.example.tiendaapp.network.api.ProductApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    // IMPORTANTE: Cambia esta URL según tu caso
    // Para emulador Android: http://10.0.2.2:8080/
    // Para dispositivo físico: http://TU_IP_LOCAL:8080/ (ej: http://192.168.1.100:8080/)
    // Para producción: tu dominio real
    private const val BASE_URL = "http://10.0.2.2:8080/"
    
    // OkHttp con logging para debugging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Retrofit instance
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // API Services
    val productApi: ProductApiService by lazy {
        retrofit.create(ProductApiService::class.java)
    }
    
    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
    
    val cartApi: CartApiService by lazy {
        retrofit.create(CartApiService::class.java)
    }
    
    val orderApi: OrderApiService by lazy {
        retrofit.create(OrderApiService::class.java)
    }
}
