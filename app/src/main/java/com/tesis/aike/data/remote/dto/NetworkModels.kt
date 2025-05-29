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