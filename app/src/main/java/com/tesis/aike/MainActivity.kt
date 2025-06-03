package com.tesis.aike

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tesis.aike.ui.components.products.ProductScreen
import com.tesis.aike.ui.home.HomeScreen
import com.tesis.aike.ui.login.LoginScreen
import com.tesis.aike.ui.profile.ProfileScreen
import com.tesis.aike.ui.qrcode.QrCodeScreen
import com.tesis.aike.ui.reservation.ReservationScreen
import com.tesis.aike.ui.theme.AikeTheme

object AppRoutes {
    const val USERNAME_ARG = "username"

    const val LOGIN_ROUTE = "login_flow"
    const val MAIN_APP_GRAPH_ROUTE = "main_app_graph"

    private const val HOME_ROUTE_BASE = "home"
    const val HOME_SCREEN_WITH_ARG = "$HOME_ROUTE_BASE/{$USERNAME_ARG}"

    private const val QR_CODE_ROUTE_BASE = "qrcode"
    const val QR_CODE_SCREEN_WITH_ARG = "$QR_CODE_ROUTE_BASE/{$USERNAME_ARG}"

    private const val PROFILE_ROUTE_BASE = "profile"
    const val PROFILE_SCREEN_WITH_ARG = "$PROFILE_ROUTE_BASE/{$USERNAME_ARG}"

    private const val RESERVATION_ROUTE_BASE = "reservation"
    const val RESERVATION_SCREEN_WITH_ARG = "$RESERVATION_ROUTE_BASE/{$USERNAME_ARG}"

    private const val PRODUCTS_ROUTE_BASE = "products" // Nueva ruta base
    const val PRODUCTS_SCREEN_WITH_ARG = "$PRODUCTS_ROUTE_BASE/{$USERNAME_ARG}" // Nueva ruta con argumento

    fun homeScreenWithUsername(username: String) = "$HOME_ROUTE_BASE/$username"
    fun qrCodeScreenWithUsername(username: String) = "$QR_CODE_ROUTE_BASE/$username"
    fun profileScreenWithUsername(username: String) = "$PROFILE_ROUTE_BASE/$username"
    fun reservationScreenWithUsername(username: String) = "$RESERVATION_ROUTE_BASE/$username"
    fun productsScreenWithUsername(username: String) = "$PRODUCTS_ROUTE_BASE/$username" // Nueva función helper

    val HOME_BASE = HOME_ROUTE_BASE
    val QR_CODE_BASE = QR_CODE_ROUTE_BASE
    val PROFILE_BASE = PROFILE_ROUTE_BASE
    val RESERVATION_BASE = RESERVATION_ROUTE_BASE
    val PRODUCTS_BASE = PRODUCTS_ROUTE_BASE // Nueva base
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AikeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN_ROUTE
    ) {
        composable(route = AppRoutes.LOGIN_ROUTE) {
            LoginScreen(navController = navController)
        }

        navigation(
            startDestination = AppRoutes.HOME_SCREEN_WITH_ARG,
            route = AppRoutes.MAIN_APP_GRAPH_ROUTE
        ) {
            composable(
                route = AppRoutes.HOME_SCREEN_WITH_ARG,
                arguments = listOf(navArgument(AppRoutes.USERNAME_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val username = backStackEntry.arguments?.getString(AppRoutes.USERNAME_ARG)
                if (username != null) {
                    HomeScreen(navController = navController, username = username)
                } else {
                    Text("Error: Nombre de usuario no encontrado para Home.")
                }
            }
            composable(
                route = AppRoutes.QR_CODE_SCREEN_WITH_ARG,
                arguments = listOf(navArgument(AppRoutes.USERNAME_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val username = backStackEntry.arguments?.getString(AppRoutes.USERNAME_ARG)
                if (username != null) {
                    QrCodeScreen(navController = navController, username = username)
                } else {
                    Text("Error: Nombre de usuario no encontrado para QR.")
                }
            }
            composable(
                route = AppRoutes.PROFILE_SCREEN_WITH_ARG,
                arguments = listOf(navArgument(AppRoutes.USERNAME_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val username = backStackEntry.arguments?.getString(AppRoutes.USERNAME_ARG)
                if (username != null) {
                    ProfileScreen(navController = navController, username = username)
                } else {
                    Text("Error: Nombre de usuario no encontrado para Perfil.")
                }
            }
            composable(
                route = AppRoutes.RESERVATION_SCREEN_WITH_ARG,
                arguments = listOf(navArgument(AppRoutes.USERNAME_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val username = backStackEntry.arguments?.getString(AppRoutes.USERNAME_ARG)
                if (username != null) {
                    ReservationScreen(navController = navController, username = username)
                } else {
                    Text("Error: Nombre de usuario no encontrado para Reserva.")
                }
            }
            composable( // Añade el composable para ProductScreen
                route = AppRoutes.PRODUCTS_SCREEN_WITH_ARG,
                arguments = listOf(navArgument(AppRoutes.USERNAME_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val username = backStackEntry.arguments?.getString(AppRoutes.USERNAME_ARG)
                if (username != null) {
                    ProductScreen(navController = navController, username = username) // Llama a tu futuro ProductScreen
                } else {
                    Text("Error: Nombre de usuario no encontrado para Productos.")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AikeTheme {
        AppNavigation()
    }
}