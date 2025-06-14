package com.tesis.aike.util

import android.util.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object JwtDecoder {

    fun getUserIdFromToken(token: String): String? {
        return try {
            val parts = token.split('.')
            if (parts.size < 2) {
                return null
            }

            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            val jsonObject = Json.parseToJsonElement(decodedString).jsonObject
            jsonObject["sub"]?.jsonPrimitive?.content
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}