package com.tesis.aike.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequest(
    val productId: Long,
    val quantity: Int
)

@Serializable
data class CartPaymentRequest(
    val items: List<CartItemRequest>,
    val userId: Long?,
    val payerEmail: String?
)

@Serializable
data class PaymentResponseMercadoPago(
    val paymentId: String,
    val status: String,
    val detail: String
)