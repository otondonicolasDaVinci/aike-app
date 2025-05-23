package com.tesis.aike.ui.home // O el paquete donde lo tengas

import android.app.Application // Importa Application
import androidx.lifecycle.AndroidViewModel // Cambia ViewModel a AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tesis.aike.data.remote.api.ChatApiService
import com.tesis.aike.domain.model.ChatMessage // Asegúrate que la ruta a ChatMessage sea correcta
import com.tesis.aike.util.TokenManager // Importa tu TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Cambia la herencia de ViewModel a AndroidViewModel y añade el constructor
class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val chatApiService = ChatApiService() // Instancia de nuestro servicio
    private val appContext = application.applicationContext // Contexto para TokenManager

    // Flujo privado mutable para la lista de mensajes
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    // Flujo público inmutable expuesto a la UI
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        // Añadir el mensaje del usuario a la lista inmediatamente
        val userMessage = ChatMessage(text = userInput, isFromUser = true)
        _messages.update { currentMessages -> currentMessages + userMessage }

        // Limpiar cualquier error previo y mostrar carga
        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            // 2. Obtener el token usando TokenManager
            val token = TokenManager.getToken(appContext)

            if (token == null) {
                // 3. Manejar el caso en que no hay token
                _errorMessage.value = "Error: No autenticado. Por favor, inicie sesión de nuevo."
                _isLoading.value = false
                _messages.update { currentMessages ->
                    currentMessages + ChatMessage(
                        "No estás autenticado. Por favor, cierra y vuelve a iniciar sesión.",
                        false // Mensaje del "sistema" o IA
                    )
                }
                return@launch // No continuar si no hay token
            }

            try {
                // 4. Pasar el token a chatApiService.sendMessage
                val response = chatApiService.sendMessage(userInput, token) // Ahora pasas el token
                if (response != null) {
                    val aiMessage = ChatMessage(text = response.respuesta, isFromUser = false)
                    _messages.update { currentMessages -> currentMessages + aiMessage }
                } else {
                    _messages.update { currentMessages ->
                        currentMessages + ChatMessage(
                            "Error: No se recibió respuesta del servidor.",
                            false
                        )
                    }
                    _errorMessage.value = "No se pudo obtener respuesta del servidor."
                    // Si la respuesta fue nula porque el token era inválido (401/403),
                    // ChatApiService ya imprimió un mensaje. Aquí podrías tomar acciones adicionales,
                    // como limpiar el token localmente con TokenManager.clearToken(appContext)
                    // y pedir al usuario que re-loguee.
                }
            } catch (e: Exception) {
                println("ChatViewModel - Error en sendMessage: ${e.message}")
                _messages.update { currentMessages ->
                    currentMessages + ChatMessage("Error de conexión: ${e.message}", false)
                }
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}