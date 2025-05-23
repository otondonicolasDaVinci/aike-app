package com.tesis.aike

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
// El import de padding no se usa directamente aquí ahora, pero puede ser útil
// import androidx.compose.foundation.layout.padding
// Scaffold y Text no se usan directamente aquí ahora
// import androidx.compose.material3.Scaffold
// import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme // Import necesario
import androidx.compose.material3.Surface // Import necesario
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController // Import para NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost // Import para NavHost
import androidx.navigation.compose.composable // Import para composable
import androidx.navigation.compose.rememberNavController // Import para rememberNavController
import androidx.navigation.navArgument
import com.tesis.aike.ui.home.HomeScreen
import com.tesis.aike.ui.login.LoginScreen
import com.tesis.aike.ui.theme.AikeTheme

object AppRoutes {
    const val LOGIN_SCREEN = "login"
    private const val HOME_SCREEN_ROUTE = "home"
    const val USERNAME_ARG = "username"
    const val HOME_SCREEN = "$HOME_SCREEN_ROUTE/{$USERNAME_ARG}"

    fun homeScreenWithUsername(username: String) = "$HOME_SCREEN_ROUTE/$username"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Mantienes esto si lo deseas para el diseño de borde a borde
        setContent {
            AikeTheme {
                Surface( // Es buena práctica envolver con Surface para el tema
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 2. LLAMA A TU FUNCIÓN DE NAVEGACIÓN PRINCIPAL
                    AppNavigation()
                }
            }
        }
    }
}

// 3. CREA TU FUNCIÓN COMPOSABLE PARA LA NAVEGACIÓN
@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN_SCREEN // Pantalla inicial
    ) {
        composable(route = AppRoutes.LOGIN_SCREEN) {
            // Llama a LoginScreen, pasándole el navController
            // Asumo que tu LoginScreen ya está modificada para aceptar NavController
            LoginScreen(navController = navController)
        }

        composable(
            route = AppRoutes.HOME_SCREEN, // Usa la ruta con el placeholder: "home/{username}"
            arguments = listOf(navArgument(AppRoutes.USERNAME_ARG) { type = NavType.StringType }) // Define el argumento
        ) { backStackEntry ->
            // Extrae el argumento de username
            val username = backStackEntry.arguments?.getString(AppRoutes.USERNAME_ARG)
            if (username != null) {
                HomeScreen(
                    navController = navController,
                    username = username
                ) // Pasa el username a HomeScreen
            } else {
                // Manejar el caso donde el username es nulo
                androidx.compose.material3.Text("Error: Nombre de usuario no encontrado.")
            }
        } // <--- ESTA LLAVE DE CIERRE DEL composable(AppRoutes.HOME_SCREEN) ESTABA BIEN
    } // <--- ESTA ES LA LLAVE DE CIERRE QUE FALTABA PARA EL NavHost
}