package com.example.tiendaapp.network.external

import retrofit2.Response
import retrofit2.http.GET

interface ExternalApiService {
    @GET("products")
    suspend fun getProducts(): Response<ExternalProductsResponse>
}
