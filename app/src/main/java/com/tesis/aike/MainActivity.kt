package com.tesis.aike

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tesis.aike.ui.MainScreen
import com.tesis.aike.ui.login.LoginScreen
import com.tesis.aike.ui.theme.AikeTheme
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object AppRoutes {
    const val USERNAME_ARG = "username"
    const val LOGIN_ROUTE = "login"
    const val MAIN_APP_ROUTE = "main/{$USERNAME_ARG}"

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
    }
}