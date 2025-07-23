package com.tesis.aike.data.remote.api

import com.tesis.aike.data.remote.dto.CartPaymentRequest
import com.tesis.aike.data.remote.dto.PaymentResponseMercadoPago
import com.tesis.aike.util.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class PaymentApiService(private val client: HttpClient = KtorClientProvider.client) {

    private val paymentUrl = "${Constants.API_BASE_URL}/api/product-payments"

    suspend fun createPaymentPreference(
        token: String?,
        request: CartPaymentRequest
    ): PaymentResponseMercadoPago? {
        if (token.isNullOrBlank()) {
            println("PaymentApiService - Error: Token no proporcionado para crear pago.")
            return null
        }

        return try {
            val response: HttpResponse = client.post(paymentUrl) {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.OK) {
                response.body<PaymentResponseMercadoPago>()
            } else {
                println("PaymentApiService - Error: ${response.status.value} - ${response.status.description}")
                try {
                    println("PaymentApiService - Cuerpo del error: ${response.bodyAsText()}")
                } catch (readEx: Exception) {
                    println("PaymentApiService - No se pudo leer cuerpo del error.")
                }
                null
            }
        } catch (e: Exception) {
            println("PaymentApiService - Excepción en createPaymentPreference: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}