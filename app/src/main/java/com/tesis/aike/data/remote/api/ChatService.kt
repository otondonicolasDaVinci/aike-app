package com.tesis.aike.data.remote.api

import com.tesis.aike.data.remote.dto.ChatRequest
import com.tesis.aike.data.remote.dto.ChatResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class ChatService(private val client: HttpClient = KtorClientProvider.client) {

    private val chatEndpointUrl = "http://10.0.2.2:8082/v1/aike/ia/text/prompt"

    suspend fun sendMessage(userInput: String, token: String?): ChatResponse? {
        if (token.isNullOrBlank()) {
            println("ChatService - Error: Token no proporcionado para sendMessage.")
            return null
        }
        return try {
            val requestPayload = ChatRequest(prompt = userInput)
            val response: HttpResponse = client.post(chatEndpointUrl) {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody(requestPayload)
            }
            if (response.status == HttpStatusCode.OK) response.body<ChatResponse>() else {
                println("ChatService - Error en respuesta (chat): ${response.status.value}")
                if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
                    println("ChatService - Token inválido/expirado en sendMessage.")
                }
                null
            }
        } catch (e: Exception) {
            println("ChatService - Excepción en sendMessage: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}