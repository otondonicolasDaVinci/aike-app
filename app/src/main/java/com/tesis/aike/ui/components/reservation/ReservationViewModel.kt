package com.tesis.aike.ui.reservation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tesis.aike.data.remote.api.ReservationService
import com.tesis.aike.data.remote.dto.ReservationData
import com.tesis.aike.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ReservationViewModel(application: Application) : AndroidViewModel(application) {

    private val reservationService = ReservationService()
    private val appContext = application.applicationContext

    private val _activeReservation = MutableStateFlow<ReservationData?>(null)
    val activeReservation: StateFlow<ReservationData?> = _activeReservation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchUserReservations()
    }

    fun fetchUserReservations() {
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
                val reservations = reservationService.getUserReservations(userId, token)
                if (!reservations.isNullOrEmpty()) {
                    
                    
                    _activeReservation.value = reservations.firstOrNull { it.status?.equals("ACTIVA", ignoreCase = true) == true }
                        ?: reservations.firstOrNull()

                    if (_activeReservation.value == null && reservations.isNotEmpty()){
                        _errorMessage.value = "No se encontraron reservas activas."
                    } else if (_activeReservation.value == null && reservations.isEmpty()) {
                        _errorMessage.value = "No tienes reservas."
                    }

                } else if (reservations == null) { 
                    _errorMessage.value = "No se pudo obtener la información de la reserva."
                }
                else { 
                    _errorMessage.value = "No tienes reservas."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar la reserva: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun formatDisplayDate(dateString: String?): String {
        return try {
            dateString?.let {
                val localDate = LocalDate.parse(it)
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yy", Locale.getDefault())
                localDate.format(formatter)
            } ?: "N/A"
        } catch (e: Exception) {
            "N/A"
        }
    }
}