package com.tesis.aike.data.remote.api

import com.tesis.aike.data.remote.dto.UserProfileData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class UserProfileService(private val client: HttpClient = KtorClientProvider.client) {

    private val usersBaseUrl = "http://10.0.2.2:8080/users"

    suspend fun getUserProfile(userId: String, token: String?): UserProfileData? {
        if (token.isNullOrBlank() || userId.isBlank()) {
            println("UserProfileService - Error: Token o UserId no proporcionado.")
            return null
        }
        val encodedUserId = URLEncoder.encode(userId.trim(), StandardCharsets.UTF_8.toString())
        val profileUrl = "$usersBaseUrl/$encodedUserId"

        return try {
            val response: HttpResponse = client.get(profileUrl) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            if (response.status == HttpStatusCode.OK) {
                response.body<UserProfileData>()
            } else {
                println("UserProfileService - Error al obtener perfil: ${response.status.value} - ${response.status.description}")
                if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
                    println("UserProfileService - Token inválido/expirado en getUserProfile.")
                }
                null
            }
        } catch (e: Exception) {
            println("UserProfileService - Excepción en getUserProfile: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}