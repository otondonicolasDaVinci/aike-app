package com.tesis.aike.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val user: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String
)

@Serializable
data class GoogleLoginApiRequest(
    val idToken: String
)

@Serializable
data class GoogleLoginApiResponse(
    val token: String, // Tu token JWT
    val userId: Long,  // El userId de tu sistema
    val email: String
)