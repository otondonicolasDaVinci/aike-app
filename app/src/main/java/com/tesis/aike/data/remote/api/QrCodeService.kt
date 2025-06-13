package com.tesis.aike.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class QrCodeService(private val client: HttpClient = KtorClientProvider.client) {

    private val qrCodeBaseUrl = "http://10.0.2.2:8080/api/qrcode"

    suspend fun getQrCodeBase64(userId: String, token: String?): String {
        if (token.isNullOrBlank() || userId.isBlank()) {
            throw IOException("Token o UserId no proporcionado.")
        }

        val encodedUserId = URLEncoder.encode(userId.trim(), StandardCharsets.UTF_8.toString())
        val finalQrCodeUrl = "$qrCodeBaseUrl/$encodedUserId"

        val response: HttpResponse = client.get(finalQrCodeUrl) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        if (response.status == HttpStatusCode.OK) {
            return response.bodyAsText()
        } else {
            val errorBody = try {
                response.bodyAsText()
            } catch (e: Exception) {
                "Error del servidor: ${response.status.value} - ${response.status.description}"
            }
            throw IOException(errorBody)
        }
    }
}