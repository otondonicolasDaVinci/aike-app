package com.tesis.aike.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tesis.aike.data.remote.api.ChatService
import com.tesis.aike.domain.model.ChatMessage
import com.tesis.aike.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val chatService = ChatService()
    private val appContext = application.applicationContext

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _messageInputText = MutableStateFlow("")
    val messageInputText: StateFlow<String> = _messageInputText.asStateFlow()

    private val _initialWelcomePending = MutableStateFlow(true)
    val initialWelcomePending: StateFlow<Boolean> = _initialWelcomePending.asStateFlow()

    fun onMessageInputChanged(text: String) {
        _messageInputText.value = text
    }

    fun markInitialWelcomeAsShown() {
        _initialWelcomePending.value = false
    }

    fun sendMessage() {
        val currentInput = _messageInputText.value
        if (currentInput.isBlank()) return

        val userMessage = ChatMessage(text = currentInput, isFromUser = true)
        _messages.update { currentMessages -> currentMessages + userMessage }

        _errorMessage.value = null
        _isLoading.value = true
        val textToSend = currentInput
        _messageInputText.value = ""

        viewModelScope.launch {
            val token = TokenManager.getToken(appContext)

            if (token == null) {
                _errorMessage.value = "Error: No autenticado. Por favor, inicie sesión de nuevo."
                _isLoading.value = false
                _messages.update { currentMessages ->
                    currentMessages + ChatMessage(
                        "No estás autenticado. Por favor, cierra y vuelve a iniciar sesión.",
                        false
                    )
                }
                return@launch
            }

            try {
                val response = chatService.sendMessage(textToSend, token)
                if (response != null) {
                    val aiMessage = ChatMessage(text = response.lines, isFromUser = false)
                    _messages.update { currentMessages -> currentMessages + aiMessage }

                    response.nuevoToken?.let { nuevoTokenRecibido ->
                        if (nuevoTokenRecibido.isNotBlank()) {
                            TokenManager.saveToken(appContext, nuevoTokenRecibido)
                            println("Nuevo token guardado desde respuesta del chat: $nuevoTokenRecibido")
                        }
                    }
                } else {
                    _messages.update { currentMessages ->
                        currentMessages + ChatMessage(
                            "Error: No se recibió respuesta del servidor.",
                            false
                        )
                    }
                    _errorMessage.value = "No se pudo obtener respuesta del servidor."
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