package com.tesis.aike

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tesis.aike.ui.MainScreen
import com.tesis.aike.ui.components.payment.PaymentWebViewScreen
import com.tesis.aike.ui.login.LoginScreen
import com.tesis.aike.ui.theme.AikeTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object AppRoutes {
    const val USERNAME_ARG = "username"
    const val LOGIN_ROUTE = "login"
    const val MAIN_APP_ROUTE = "main/{$USERNAME_ARG}"
    const val PAYMENT_WEBVIEW_ROUTE = "payment_webview/{url}"
    const val PAYMENT_SUCCESS_ROUTE = "payment_success"
    const val PAYMENT_FAILURE_ROUTE = "payment_failure"


    fun mainScreenWithUsername(username: String): String {
        val encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8.toString())
        return "main/$encodedUsername"
    }
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
        composable(
            route = AppRoutes.MAIN_APP_ROUTE,
            arguments = listOf(navArgument(AppRoutes.USERNAME_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString(AppRoutes.USERNAME_ARG) ?: "Usuario"
            MainScreen(rootNavController = navController, username = username)
        }
        composable(
            route = AppRoutes.PAYMENT_WEBVIEW_ROUTE,
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
            val decodedUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
            PaymentWebViewScreen(
                navController = navController,
                paymentUrl = decodedUrl
            )
        }
        composable(AppRoutes.PAYMENT_SUCCESS_ROUTE) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("¡Pago Exitoso!", fontSize = 24.sp, color = Color.Green)
            }
        }
        composable(AppRoutes.PAYMENT_FAILURE_ROUTE) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("El pago falló.", fontSize = 24.sp, color = Color.Red)
            }
        }
    }
}