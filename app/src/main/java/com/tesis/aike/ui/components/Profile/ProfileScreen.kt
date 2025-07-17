package com.tesis.aike.ui.profile

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tesis.aike.AppRoutes
import com.tesis.aike.util.TokenManager

@Composable
fun ProfileScreen(rootNavController: NavController, username: String) {
    val context = LocalContext.current
    val activity = (context as? Activity)
    val profileViewModel: ProfileViewModel = viewModel()
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val isLoading by profileViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by profileViewModel.errorMessage.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Error desconocido",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        } else if (userProfile != null) {
            val profile = userProfile!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Icono de Perfil",
                    modifier = Modifier.size(120.dp).clip(CircleShape),
                    tint = Color(0xFFB39DDB)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = profile.name ?: username,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                ProfileInfoRow(label = "DNI:", value = profile.dni ?: "No disponible")
                ProfileInfoRow(label = "TEL:", value = "N/A (no en DTO)")
                ProfileInfoRow(label = "MAIL:", value = profile.email ?: "No disponible")
                ProfileInfoRow(label = "Pass:", value = "********************")

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "¿Queres modificar algún dato? Llama al hall",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = {
                        Toast.makeText(context, "Llamando a recepción...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Call, contentDescription = "Llamar", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("LLAMAR A RECEPCIÓN", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        TokenManager.clearAuthData(context)
                        rootNavController.navigate(AppRoutes.LOGIN_ROUTE) {
                            popUpTo(AppRoutes.MAIN_APP_ROUTE) {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar Sesión", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        activity?.finishAffinity()
                    },
                    modifier = Modifier.fillMaxWidth(0.9f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir de la aplicación")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Salir de la Aplicación")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            Text("No se pudo cargar la información del perfil.", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}