package com.tesis.aike.ui.components.QR

import android.app.Application
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tesis.aike.data.remote.api.QrCodeService
import com.tesis.aike.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

class QrCodeViewModel(application: Application) : AndroidViewModel(application) {

    private val qrCodeService = QrCodeService()
    private val appContext = application.applicationContext

    private val _qrCodeBitmap = MutableStateFlow<ImageBitmap?>(null)
    val qrCodeBitmap: StateFlow<ImageBitmap?> = _qrCodeBitmap.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchQrCode()
    }

    fun fetchQrCode() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _qrCodeBitmap.value = null

            val token = TokenManager.getToken(appContext)
            val userId = TokenManager.getUserId(appContext)

            if (token == null || userId == null) {
                _errorMessage.value = "Error de autenticación. Intente iniciar sesión de nuevo."
                _isLoading.value = false
                return@launch
            }

            try {
                val base64String = qrCodeService.getQrCodeBase64(userId, token)
                val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                val inputStream = ByteArrayInputStream(imageBytes)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    _qrCodeBitmap.value = bitmap.asImageBitmap()
                } else {
                    _errorMessage.value = "No se pudo decodificar la imagen QR recibida."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Ocurrió un error desconocido"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}