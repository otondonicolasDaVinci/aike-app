package com.tesis.aike.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tesis.aike.ui.home.HomeScreen
import com.tesis.aike.ui.navigation.AppBottomNavigationBar
import com.tesis.aike.ui.navigation.BottomNavItem
import com.tesis.aike.ui.profile.ProfileScreen
import com.tesis.aike.ui.components.products.ProductScreen
import com.tesis.aike.ui.qrcode.QrCodeScreen
import com.tesis.aike.ui.reservation.ReservationScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(rootNavController: NavController, username: String) {
    val bottomBarNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(navController = bottomBarNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomBarNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen(rootNavController = rootNavController, username = username) }
            composable(BottomNavItem.Reservation.route) { ReservationScreen(rootNavController = rootNavController, username = username) }
            composable(BottomNavItem.QrCode.route) { QrCodeScreen(rootNavController = rootNavController, username = username) }
            composable(BottomNavItem.Products.route) { ProductScreen(rootNavController = rootNavController, username = username) }
            composable(BottomNavItem.Profile.route) { ProfileScreen(rootNavController = rootNavController, username = username) }
        }
    }
}