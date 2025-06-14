package com.tesis.aike.data.remote.api

import com.tesis.aike.domain.model.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import com.tesis.aike.util.Constants

class ProductService(private val client: HttpClient = KtorClientProvider.client) {

    private val productsBaseUrl = "${Constants.API_BASE_URL}/products"

    suspend fun getAllProducts(token: String?): List<Product>? {
        if (token.isNullOrBlank()) {
            println("ProductService - Advertencia: Token no proporcionado para getAllProducts. Intentando sin token.")
        }

        return try {
            val response: HttpResponse = client.get(productsBaseUrl) {

                token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }

            if (response.status == HttpStatusCode.OK) {
                response.body<List<Product>>()
            } else {
                println("ProductService - Error al obtener productos: ${response.status.value} - ${response.status.description}")
                try { println("ProductService - Cuerpo del error: ${response.bodyAsText()}") }
                catch (readEx: Exception) { println("ProductService - No se pudo leer cuerpo del error.") }
                null
            }
        } catch (e: Exception) {
            println("ProductService - Excepción en getAllProducts: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}