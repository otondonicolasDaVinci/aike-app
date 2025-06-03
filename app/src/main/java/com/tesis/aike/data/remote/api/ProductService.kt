package com.tesis.aike.data.remote.api

import com.tesis.aike.domain.model.Product // Usa tu data class de Kotlin
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

class ProductService(private val client: HttpClient = KtorClientProvider.client) {

    private val productsBaseUrl = "http://10.0.2.2:8082/products" // Endpoint de tu ProductsController

    suspend fun getAllProducts(token: String?): List<Product>? {
        // El endpoint de productos podría no requerir token si es público,
        // pero es bueno tener la opción de pasarlo si fuera necesario.
        // Si no requiere token, puedes quitar el header o la verificación de token.
        if (token.isNullOrBlank()) {
            // Si es público, podrías continuar sin token. Si es protegido, retorna error.
            println("ProductService - Advertencia: Token no proporcionado para getAllProducts. Intentando sin token.")
            // return null // Descomenta si el endpoint es protegido y el token es obligatorio
        }

        return try {
            val response: HttpResponse = client.get(productsBaseUrl) {
                // Si el endpoint de productos SÍ requiere autenticación:
                token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }

            if (response.status == HttpStatusCode.OK) {
                response.body<List<Product>>() // Espera una lista de Product
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

    // Podrías añadir aquí más funciones si necesitas, por ejemplo, getProductsByCategory(category, token)
    // suspend fun getProductsByCategory(category: String, token: String?): List<Product>? { ... }
}