package com.tesis.aike.ui.login // Asegúrate que este sea tu package correcto

// Iconos para los botones sociales (aunque no los usemos para loguear ahora, los dejamos por si acaso)
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator // Asegúrate de tener este import
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Import para sp si usas MaterialTheme.typography.bodySmall
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tesis.aike.AppRoutes
import com.tesis.aike.R
import com.tesis.aike.data.remote.api.ChatApiService // Asegúrate que esta ruta sea correcta
import com.tesis.aike.data.remote.dto.AuthRequest // Asegúrate que esta ruta sea correcta
import com.tesis.aike.ui.theme.AikeTheme
import com.tesis.aike.util.TokenManager // Asegúrate que esta ruta sea correcta
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(modifier: Modifier = Modifier, navController: NavController) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var loginError by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // Es buena práctica crear la instancia del servicio una sola vez.
    // Si LoginScreen se recompone mucho, esto podría ser ineficiente.
    // Considera proveerlo a través de un ViewModel o inyección de dependencias en el futuro.
    val chatApiService = remember { ChatApiService() }


    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // He comentado la imagen aquí porque ya tienes un Text "Inicie Sesión"
            // Si quieres el logo Y el texto, puedes ajustar el espaciado.
            // Si quieres SOLO el logo, quita el Text de abajo.
            // Si quieres SOLO el texto, quita la Imagen.
            // Por ahora, dejo ambos para que decidas.
            Text(
                text = "Inicie sesión",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Image(
                painter = painterResource(id = R.drawable.aike_logo), // Asegúrate que aike_logo exista
                contentDescription = "Logo de la aplicación",
                modifier = Modifier.height(100.dp) // Puedes ajustar el tamaño
            )

            Spacer(modifier = Modifier.height(24.dp)) // Reduje un poco este Spacer

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    loginError = null
                },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    loginError = null
                },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = loginError != null
            )

            loginError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall, // Asegúrate que bodySmall esté definido o usa otra tipografía
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Forgot password?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
                    .clickable {
                        Toast
                            .makeText(context, "Funcionalidad no implementada", Toast.LENGTH_SHORT)
                            .show()
                        println("Forgot password clicked!")
                    }
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { Toast.makeText(context, "Inicio con Google no implementado", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2D3748),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Google logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Continue with Google", textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { Toast.makeText(context, "Inicio con Facebook no implementado", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2D3748),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Facebook,
                        contentDescription = "Facebook logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Continue with Facebook", textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (username == "admin" && password == "admin") {
                        isLoading = true
                        loginError = null
                        coroutineScope.launch {
                            val authRequest = AuthRequest(userId = "1", password = "123456")
                            val authResponse = chatApiService.loginUser(authRequest)

                            isLoading = false // Asegúrate de poner esto ANTES de cualquier return o navegación
                            if (authResponse != null && authResponse.token.isNotBlank()) {
                                TokenManager.saveToken(context, authResponse.token)
                                println("Token guardado: ${authResponse.token}")
                                navController.navigate(AppRoutes.homeScreenWithUsername(username)) {
                                    popUpTo(AppRoutes.LOGIN_SCREEN) { inclusive = true }
                                    launchSingleTop = true
                                }
                            } else {
                                loginError = "Error de autenticación con el servidor."
                                println("Login con API fallido o token vacío.")
                            }
                        }
                    } else {
                        loginError = "Usuario o contraseña de app incorrectos."
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B7280),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                // Muestra el CircularProgressIndicator dentro del botón cuando isLoading es true
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), // Ajusta el tamaño según necesites
                        color = Color.White, // Color del indicador
                        strokeWidth = 2.dp // Grosor del indicador
                    )
                } else {
                    Text(text = "Iniciar sesión", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No se registro? ",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Regístrese en la web",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        Toast
                            .makeText(context, "Funcionalidad no implementada", Toast.LENGTH_SHORT)
                            .show()
                        println("Regístrese en la web clicked!")
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=740dp")
@Composable
fun LoginScreenPreview() {
    AikeTheme {
        LoginScreen(navController = rememberNavController())
    }
}