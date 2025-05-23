package com.tesis.aike.data.remote.dto // O el paquete donde la tengas

import kotlinx.serialization.Serializable // <--- ¡ASEGÚRATE DE TENER ESTE IMPORT!

@Serializable // <--- ¡ESTA ANOTACIÓN ES CRUCIAL!
data class ChatResponse(
    val respuesta: String // O la estructura que tu backend devuelva
)