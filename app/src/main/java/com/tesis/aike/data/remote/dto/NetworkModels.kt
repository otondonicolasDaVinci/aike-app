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
    val token: String,
    val userId: Long,
    val email: String
)