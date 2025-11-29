package com.example.tiendaapp.network.external

import com.google.gson.annotations.SerializedName

data class ExternalProductsResponse(
    @SerializedName("products") val products: List<ExternalProduct> = emptyList(),
    @SerializedName("total") val total: Int? = null,
    @SerializedName("skip") val skip: Int? = null,
    @SerializedName("limit") val limit: Int? = null,
)

data class ExternalProduct(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("brand") val brand: String? = null,
    @SerializedName("category") val category: String? = null,
)
