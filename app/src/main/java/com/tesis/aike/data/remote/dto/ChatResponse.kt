// En tu archivo de DTOs (ej. NetworkModels.kt o ChatResponse.kt)
package com.tesis.aike.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val respuesta: String,
    @SerialName("newToken")
    val nuevoToken: String? = null
)