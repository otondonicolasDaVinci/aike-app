package com.tesis.aike.data.remote.api

import com.tesis.aike.data.remote.dto.AuthRequest
import com.tesis.aike.data.remote.dto.AuthResponse
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

class AuthService(private val client: HttpClient = KtorClientProvider.client) {

    private val baseUrl = "http://10.0.2.2:8082/auth"
    private val loginUrl = "$baseUrl/login"
    private val refreshUrl = "$baseUrl/refresh"

    suspend fun loginUser(authRequest: AuthRequest): AuthResponse? {
        return try {
            val response: HttpResponse = client.post(loginUrl) {
                contentType(ContentType.Application.Json)
                setBody(authRequest)
            }
            if (response.status == HttpStatusCode.OK) response.body<AuthResponse>() else {
                println("AuthService - Error en login: ${response.status.value}")
                null
            }
        } catch (e: Exception) {
            println("AuthService - Excepción en loginUser: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun refreshToken(oldToken: String): AuthResponse? {
        return try {
            val response: HttpResponse = client.post(refreshUrl) {
                header(HttpHeaders.Authorization, "Bearer $oldToken")
            }
            if (response.status == HttpStatusCode.OK) response.body<AuthResponse>() else {
                println("AuthService - Error al refrescar token: ${response.status.value}")
                null
            }
        } catch (e: Exception) {
            println("AuthService - Excepción al refrescar token: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}