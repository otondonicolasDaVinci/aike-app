package com.tesis.aike.ui.reservation 

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tesis.aike.AppRoutes
import com.tesis.aike.R
import com.tesis.aike.ui.home.AppBottomNavigationBar
import com.tesis.aike.ui.home.BottomNavItem
import com.tesis.aike.ui.theme.AikeTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ReservationScreen(navController: NavController, username: String) { 
    val reservationViewModel: ReservationViewModel = viewModel()
    val activeReservation by reservationViewModel.activeReservation.collectAsStateWithLifecycle()
    val isLoading by reservationViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by reservationViewModel.errorMessage.collectAsStateWithLifecycle()

    val bottomNavItems = listOf(
        BottomNavItem.Viking,
        BottomNavItem.Hut,
        BottomNavItem.Key,
        BottomNavItem.Bag,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                navController = navController,
                items = bottomNavItems,
                currentUsername = username, 
                onVikingTabAlreadyHome = {
                    val homeRoute = AppRoutes.homeScreenWithUsername(username)
                    if (navController.currentDestination?.route != homeRoute) {
                        navController.navigate(homeRoute) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
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
            } else if (activeReservation != null) {
                val reservation = activeReservation!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Mi reserva",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val checkInDate = reservationViewModel.formatDisplayDate(reservation.startDate)
                    val checkOutDate = reservationViewModel.formatDisplayDate(reservation.endDate)
                    Text("Check in: $checkInDate - Check out: $checkOutDate", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    InfoRow(label = "Huespedes:", value = "N/A (no en DTO)")
                    InfoRow(label = "Cabaña:", value = reservation.cabin?.name ?: "N/A")
                    InfoRow(label = "Estado:", value = reservation.status ?: "N/A")

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Telefonos utiles:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    UsefulContactsCategory("Emergencias:")
                    UsefulContactItem("- 911 (Policía Federal)")
                    UsefulContactItem("- 107 (Emergencias Médicas)")

                    UsefulContactsCategory("Hospitales:")
                    UsefulContactItem("- Hospital Regional de Ushuaia: +54 2901 423100")
                    UsefulContactItem("- Hospital Regional de Río Grande: +54 2964 432100")

                    UsefulContactsCategory("Turismo:")
                    UsefulContactItem("- Secretaría de Turismo de Tierra del Fuego: +54 2901 430640")

                    UsefulContactsCategory("Transporte:")
                    UsefulContactItem("- Aeropuerto Internacional de Ushuaia: +54 2901 422310")
                    UsefulContactItem("- Aeropuerto Internacional de Río Grande: +54 2964 422244")

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Donde esta tu cabaña:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Image(
                        painter = painterResource(id = R.drawable.mapa_ubicacion_cabana),
                        contentDescription = "Ubicación de la cabaña",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Text("No hay información de reserva disponible.", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun UsefulContactsCategory(categoryName: String) {
    Text(
        text = categoryName,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun UsefulContactItem(contactInfo: String) {
    Text(
        text = contactInfo,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 2.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun ReservationScreenPreview() {
    AikeTheme {
        ReservationScreen(navController = rememberNavController(), username = "UsuarioPreview")
    }
}