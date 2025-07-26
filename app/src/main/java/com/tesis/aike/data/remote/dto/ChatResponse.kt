package com.tesis.aike.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val lines: String,
    @SerialName("newToken")
    val nuevoToken: String? = null
)