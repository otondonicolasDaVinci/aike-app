package com.tesis.aike.ui.profile

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tesis.aike.AppRoutes
import com.tesis.aike.ui.home.AppBottomNavigationBar
import com.tesis.aike.ui.home.BottomNavItem
import com.tesis.aike.ui.theme.AikeTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavController, username: String) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel()
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val isLoading by profileViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by profileViewModel.errorMessage.collectAsStateWithLifecycle()

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (userProfile != null) {
                val profile = userProfile!!
                Spacer(modifier = Modifier.height(16.dp))
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Icono de Perfil",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    tint = Color(0xFFB39DDB)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = profile.name ?: username,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                ProfileDetailItem(label = "DNI:", value = profile.dni ?: "No disponible")
                ProfileDetailItem(label = "TEL:", value = "+54 9 11 3939-8945")
                ProfileDetailItem(label = "MAIL:", value = profile.email ?: "No disponible")
                ProfileDetailItem(label = "Pass:", value = "********************")

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "¿Queres modificar algún dato? Llama al hall",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = {
                        Toast.makeText(context, "Funcionalidad de llamada no implementada", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(50.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Call, contentDescription = "Llamar", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("LLAMAR A RECEPCIÓN", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

            } else {
                Text("No se pudo cargar la información del perfil.", modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
private fun ProfileDetailItem(label: String, value: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    AikeTheme {
        ProfileScreen(navController = rememberNavController(), username = "PedroPreview")
    }
}