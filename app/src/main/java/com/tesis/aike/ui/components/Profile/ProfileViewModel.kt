package com.tesis.aike.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tesis.aike.data.remote.api.UserProfileService
import com.tesis.aike.data.remote.dto.UserProfileData
import com.tesis.aike.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userProfileService = UserProfileService()
    private val appContext = application.applicationContext

    private val _userProfile = MutableStateFlow<UserProfileData?>(null)
    val userProfile: StateFlow<UserProfileData?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val token = TokenManager.getToken(appContext)
            val userId = TokenManager.getUserId(appContext)

            if (token == null || userId == null) {
                _errorMessage.value = "Error de autenticación. Intente iniciar sesión de nuevo."
                _isLoading.value = false
                return@launch
            }

            try {
                val profile = userProfileService.getUserProfile(userId, token)
                if (profile != null) {
                    _userProfile.value = profile
                } else {
                    _errorMessage.value = "No se pudo obtener la información del perfil."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el perfil: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}