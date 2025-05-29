package com.tesis.aike.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

class QrCodeService(private val client: HttpClient = KtorClientProvider.client) {

    private val qrCodeBaseUrl = "http://10.0.2.2:8082/api/qrcode"

    suspend fun getQrCodeBase64(userId: String, token: String?): String? {
        if (token.isNullOrBlank() || userId.isBlank()) {
            println("QrCodeService - Error: Token o UserId no proporcionado.")
            return null
        }
        val finalQrCodeUrl = "$qrCodeBaseUrl/$userId"
        return try {
            val response: HttpResponse = client.get(finalQrCodeUrl) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            if (response.status == HttpStatusCode.OK) response.bodyAsText() else {
                println("QrCodeService - Error al obtener QR: ${response.status.value}")
                if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
                    println("QrCodeService - Token inválido/expirado en getQrCodeBase64.")
                } else {
                    try { println("QrCodeService - Cuerpo del error QR: ${response.bodyAsText()}") }
                    catch (readEx: Exception) { println("QrCodeService - No se pudo leer cuerpo del error QR.") }
                }
                null
            }
        } catch (e: Exception) {
            println("QrCodeService - Excepción en getQrCodeBase64: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}