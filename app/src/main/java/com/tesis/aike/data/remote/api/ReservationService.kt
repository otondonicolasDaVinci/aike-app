package com.tesis.aike.data.remote.api

import com.tesis.aike.data.remote.dto.ReservationData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

class ReservationService(private val client: HttpClient = KtorClientProvider.client) {

    private val reservationsBaseUrl = "http://10.0.2.2:8082/reservations"

    suspend fun getUserReservations(userId: String, token: String?): List<ReservationData>? {
        if (token.isNullOrBlank() || userId.isBlank()) {
            println("ReservationService - Error: Token o UserId no proporcionado.")
            return null
        }
        val url = "$reservationsBaseUrl/user/$userId"
        return try {
            val response: HttpResponse = client.get(url) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            if (response.status == HttpStatusCode.OK) {
                response.body<List<ReservationData>>()
            } else {
                println("ReservationService - Error al obtener reservas: ${response.status.value} - ${response.status.description}")
                if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
                    println("ReservationService - Token inválido/expirado.")
                } else {
                    try { println("ReservationService - Cuerpo del error: ${response.bodyAsText()}") }
                    catch (readEx: Exception) { println("ReservationService - No se pudo leer cuerpo del error.") }
                }
                null
            }
        } catch (e: Exception) {
            println("ReservationService - Excepción en getUserReservations: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}