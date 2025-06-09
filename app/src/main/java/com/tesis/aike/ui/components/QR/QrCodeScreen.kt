package com.tesis.aike.ui.components.QR

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tesis.aike.AppRoutes
import com.tesis.aike.ui.components.QR.QrCodeViewModel
import com.tesis.aike.ui.home.AppBottomNavigationBar
import com.tesis.aike.ui.home.BottomNavItem
import com.tesis.aike.ui.theme.AikeTheme



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun QrCodeScreen(
    navController: NavController,
    username: String
) {
    val qrViewModel: QrCodeViewModel = viewModel()
    val qrBitmap by qrViewModel.qrCodeBitmap.collectAsStateWithLifecycle()
    val isLoading by qrViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by qrViewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        qrViewModel.fetchQrCode()
    }

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
                currentUsername = username, // Pasa el username
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
                .background(Color.Black)
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else if (errorMessage != null) {
                Text(text = errorMessage ?: "Error desconocido", color = Color.Red)
            } else if (qrBitmap != null) {
                Image(
                    bitmap = qrBitmap!!,
                    contentDescription = "Código QR para desbloqueo",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text("Generando código QR...", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QrCodeScreenPreview() {
    AikeTheme {
        QrCodeScreen(navController = rememberNavController(), username = "UsuarioPreview")
    }
}