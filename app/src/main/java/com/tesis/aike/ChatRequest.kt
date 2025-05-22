package com.tesis.aike // O el paquete donde la tengas

import kotlinx.serialization.Serializable // <--- ¡ASEGÚRATE DE TENER ESTE IMPORT!

@Serializable // <--- ¡ESTA ANOTACIÓN ES CRUCIAL Y DEBE ESTAR JUSTO ENCIMA DE LA CLASE!
data class ChatRequest(
    val prompt: String // Como lo habíamos modificado para tu backend
)