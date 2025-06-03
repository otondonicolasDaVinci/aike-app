package com.tesis.aike.domain.model

import kotlinx.serialization.Serializable
import com.tesis.aike.R // Asegúrate que R esté importado si usas drawable directamente

@Serializable
data class Product(
    val id: String,
    val title: String,
    val price: Double,
    val description: String,
    val imageUrl: String? = null,
    val category: String
)

data class CartItem(
    val product: Product,
    var quantity: Int
)