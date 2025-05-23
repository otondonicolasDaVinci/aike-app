package com.tesis.aike.data.remote.api

import com.tesis.aike.data.remote.dto.AuthRequest
import com.tesis.aike.data.remote.dto.AuthResponse
import com.tesis.aike.data.remote.dto.ChatRequest
import com.tesis.aike.data.remote.dto.ChatResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header // Import para la función header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders // Import para HttpHeaders.Authorization
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

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

    // URLs de los Endpoints
    private val authLoginUrl = "http://10.0.2.2:8082/auth/login"
    private val chatEndpointUrl = "http://10.0.2.2:8082/v1/aike/ia/text/prompt"

    // Función para el login con el backend
    suspend fun loginUser(authRequest: AuthRequest): AuthResponse? {
        return try {
            val response: HttpResponse = client.post(authLoginUrl) {
                contentType(ContentType.Application.Json)
                setBody(authRequest)
            }

            if (response.status == HttpStatusCode.OK) {
                response.body<AuthResponse>()
            } else {
                println("Error en login con backend: ${response.status.value} - ${response.status.description}")
                null
            }
        } catch (e: Exception) {
            println("Excepción en loginUser: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    // Función sendMessage MODIFICADA para aceptar y usar el token
    suspend fun sendMessage(userInput: String, token: String?): ChatResponse? {
        if (token == null) {
            println("Error: Token de autenticación no proporcionado para sendMessage.")
            // Considera devolver un tipo de error específico o lanzar una excepción aquí
            // para un manejo más robusto en el ViewModel.
            return null
        }

        return try {
            val requestPayload = ChatRequest(prompt = userInput) // Usamos userInput para el campo prompt
            val response: HttpResponse = client.post(chatEndpointUrl) {
                contentType(ContentType.Application.Json)
                // Añade la cabecera de Autorización con el Bearer Token
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody(requestPayload)
            }

            if (response.status == HttpStatusCode.OK) {
                response.body<ChatResponse>()
            } else {
                println("Error en la respuesta del servidor (chat): ${response.status.value} - ${response.status.description}")
                // Puedes añadir manejo específico para ciertos códigos de error, como 401 (Unauthorized)
                if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
                    println("Token inválido o expirado. Intente iniciar sesión nuevamente.")
                    // Aquí podrías tener lógica para invalidar el token guardado o pedir re-login.
                }
                null
            }
        } catch (e: Exception) {
            println("Error al realizar la petición (chat) a '$chatEndpointUrl': ${e.message}")
            e.printStackTrace()
            null
        }
    }
}