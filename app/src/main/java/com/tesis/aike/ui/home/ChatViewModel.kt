package com.tesis.aike.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tesis.aike.data.remote.api.ChatApiService
import com.tesis.aike.domain.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val chatApiService = ChatApiService() // Instancia de nuestro servicio

    // Flujo privado mutable para la lista de mensajes
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    // Flujo público inmutable expuesto a la UI
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // Podríamos añadir estados para carga y errores también
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        // 1. Añadir el mensaje del usuario a la lista inmediatamente
        val userMessage = ChatMessage(text = userInput, isFromUser = true)
        _messages.update { currentMessages -> currentMessages + userMessage }

        // 2. Limpiar cualquier error previo y mostrar carga
        _errorMessage.value = null
        _isLoading.value = true

        // 3. Llamar al backend
        viewModelScope.launch { // Las llamadas de red se hacen en una coroutine del ViewModel
            try {
                val response = chatApiService.sendMessage(userInput)
                if (response != null) {
                    val aiMessage = ChatMessage(text = response.respuesta, isFromUser = false)
                    _messages.update { currentMessages -> currentMessages + aiMessage }
                } else {
                    // Manejar respuesta nula del servicio (error ya logueado en el servicio)
                    _messages.update { currentMessages ->
                        currentMessages + ChatMessage(
                            "Error: No se recibió respuesta del servidor.",
                            false
                        )
                    }
                    _errorMessage.value = "No se pudo obtener respuesta del servidor."
                }
            } catch (e: Exception) {
                // Manejar excepción de la llamada de red
                println("ChatViewModel - Error en sendMessage: ${e.message}")
                _messages.update { currentMessages ->
                    currentMessages + ChatMessage("Error: ${e.message}", false)
                }
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false // Ocultar carga
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}