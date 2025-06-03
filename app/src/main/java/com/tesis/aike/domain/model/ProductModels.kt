package com.tesis.aike.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Long,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val category: String
)

data class CartItem(
    val product: Product,
    var quantity: Int
)