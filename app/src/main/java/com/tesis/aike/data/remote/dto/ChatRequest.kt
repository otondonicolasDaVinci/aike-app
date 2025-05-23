package com.tesis.aike.data.remote.dto // O el paquete donde la tengas

import kotlinx.serialization.Serializable // <-- VERIFICA ESTE IMPORT

@Serializable // <-- VERIFICA ESTA ANOTACIÓN
data class ChatRequest(
    val prompt: String
)