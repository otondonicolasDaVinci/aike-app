package com.tesis.aike.data.remote.dto // O el paquete que uses para DTOs/modelos de red

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoleData(
    val id: Long? = null,
    val name: String? = null
)

@Serializable
data class UserProfileData(
    val id: Long? = null,
    val name: String? = null,
    val email: String? = null,
    val dni: String? = null,
    @SerialName("role")
    val roleData: RoleData? = null
)