package com.tesis.aike.data.remote.api

// Imports para ChatRequest y ChatResponse

import com.tesis.aike.data.remote.dto.ChatRequest
import com.tesis.aike.data.remote.dto.ChatResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// 1. MODIFICACIÓN CLAVE: Asegúrate de que tu data class ChatRequest use "prompt"
//    Si ChatRequest está en otro archivo (ej. NetworkModels.kt), haz este cambio allí.
//    Si no, y la tienes definida aquí mismo o la vas a definir, debe ser así:
//
//    import kotlinx.serialization.Serializable // Si defines ChatRequest aquí
//
//    @Serializable
//    data class ChatRequest(
//        val prompt: String // <--- CAMBIO IMPORTANTE: de "pregunta" a "prompt"
//    )

class ChatApiService {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    // Mantén tu endpointUrl como lo tienes configurado
    private val endpointUrl = "http://localhost:8082/v1/aike/ia/text/prompt"

    suspend fun sendMessage(userInput: String): ChatResponse? { // Cambiamos el nombre del parámetro a userInput para claridad
        return try {
            // 2. Crea el objeto de la petición usando la clave "prompt"
            val requestPayload = ChatRequest(prompt = userInput) // Usamos el parámetro 'userInput'

            val response: HttpResponse = client.post(endpointUrl) {
                contentType(ContentType.Application.Json)
                setBody(requestPayload)
            }

            if (response.status == HttpStatusCode.OK) {
                val chatResponse = response.body<ChatResponse>()
                chatResponse
            } else {
                println("Error en la respuesta del servidor: ${response.status.value} - ${response.status.description}")
                null
            }
        } catch (e: Exception) {
            println("Error al realizar la petición a '$endpointUrl': ${e.message}")
            e.printStackTrace()
            null
        }
    }

    // fun close() {
    //     client.close()
    // }
}