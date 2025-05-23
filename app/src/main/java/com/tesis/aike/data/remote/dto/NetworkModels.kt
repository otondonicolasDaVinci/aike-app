package com.tesis.aike.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val userId: String,
    val password: String
)

@Serializable
data class AuthResponse(
    // Asumo que el token viene en una clave "token".
    // Si tu backend usa una clave diferente (ej: "accessToken", "bearerToken"),
    // CAMBIA "token" por el nombre correcto de la clave.
    val token: String
    // Podría haber otros campos como tokenType, expiresIn, etc.
    // val tokenType: String? = null // Ejemplo
)