package com.tesis.aike.data.remote.api // O tu paquete

import com.tesis.aike.data.remote.dto.AuthRequest // Para el login normal
import com.tesis.aike.data.remote.dto.AuthResponse // Para el login normal y refresh
import com.tesis.aike.data.remote.dto.GoogleLoginApiRequest // NUEVA
import com.tesis.aike.data.remote.dto.GoogleLoginApiResponse // NUEVA
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

    private val baseUrl = "http://10.0.2.2:8080"
    private val regularLoginUrl = "$baseUrl/auth/login"
    private val googleLoginUrl = "$baseUrl/auth/login-google"
    private val refreshUrl = "$baseUrl/auth/refresh"

    suspend fun loginUser(authRequest: AuthRequest): AuthResponse? {
        return try {
            val response: HttpResponse = client.post(regularLoginUrl) {
                contentType(ContentType.Application.Json)
                setBody(authRequest)
            }
            if (response.status == HttpStatusCode.OK) response.body<AuthResponse>() else {
                println("AuthService - Error en login regular: ${response.status.value}")
                null
            }
        } catch (e: Exception) {
            println("AuthService - Excepción en loginUser: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun loginWithGoogleToken(idToken: String): GoogleLoginApiResponse? {
        val requestBody = GoogleLoginApiRequest(idToken = idToken)
        return try {
            val response: HttpResponse = client.post(googleLoginUrl) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            if (response.status == HttpStatusCode.OK) {
                response.body<GoogleLoginApiResponse>()
            } else {
                println("AuthService - Error en login con Google Token: ${response.status.value}")
                println("AuthService - Cuerpo del error: ${response.body<String>()}") // Intenta leer el cuerpo como String
                null
            }
        } catch (e: Exception) {
            println("AuthService - Excepción en loginWithGoogleToken: ${e.message}")
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