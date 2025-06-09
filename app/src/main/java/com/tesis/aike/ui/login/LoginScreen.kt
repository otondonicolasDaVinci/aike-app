package com.tesis.aike.ui.login

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.tesis.aike.AppRoutes
import com.tesis.aike.R
import com.tesis.aike.data.remote.api.AuthService
import com.tesis.aike.data.remote.dto.AuthRequest
import com.tesis.aike.ui.theme.AikeTheme
import com.tesis.aike.util.TokenManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(modifier: Modifier = Modifier, navController: NavController) {
    var uiUsername by rememberSaveable { mutableStateOf("") }
    var uiPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var loginError by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isLoadingGoogle by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authService = remember { AuthService() }

    val googleSignInClient: GoogleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("959565422604-0dvq3jihm4as00tukaut3i60j2ssm25o.apps.googleusercontent.com")
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        isLoadingGoogle = false
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    Log.d("LoginScreen", "Google ID Token obtenido")
                    coroutineScope.launch {
                        isLoadingGoogle = true
                        try {
                            val apiResponse = authService.loginWithGoogleToken(idToken)
                            if (apiResponse != null && apiResponse.token.isNotBlank()) {
                                TokenManager.saveAuthData(context, apiResponse.token, apiResponse.userId.toString())
                                Log.d("LoginScreen", "Token de API guardado. UserID: ${apiResponse.userId}")
                                val displayNameForHome = account.email ?: uiUsername.takeIf { it.isNotBlank() } ?: "UsuarioGoogle"
                                navController.navigate(AppRoutes.homeScreenWithUsername(displayNameForHome)) {
                                    popUpTo(AppRoutes.LOGIN_ROUTE) { inclusive = true }
                                    launchSingleTop = true
                                }
                            } else {
                                loginError = "Error de autenticación con servidor vía Google."
                                Log.e("LoginScreen", "loginWithGoogleToken fallido o token API vacío")
                            }
                        } catch (e: Exception) {
                            loginError = "Error inesperado al contactar al servidor."
                            Log.e("LoginScreen", "Excepción en coroutine de Google Login", e)
                        } finally {
                            isLoadingGoogle = false
                        }
                    }
                } else {
                    loginError = "No se pudo obtener el token de Google."
                    Log.e("LoginScreen", "Google ID Token es nulo después de un sign-in exitoso.")
                }
            } catch (e: ApiException) {
                loginError = "Error de Google Sign-In: ${e.statusCode}"
                Log.e("LoginScreen", "Error de Google Sign-In", e)
            }
        } else {
            loginError = "Inicio de sesión con Google cancelado o fallido."
            Log.w("LoginScreen", "Google Sign-In fallido, resultCode: ${result.resultCode}")
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                painter = painterResource(id = R.drawable.aike_logo),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier.height(120.dp)
            )
            Text(
                text = "Bienvenido a Aike",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiUsername,
                onValueChange = {
                    uiUsername = it
                    loginError = null
                },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Icono de Usuario") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiPassword,
                onValueChange = {
                    uiPassword = it
                    loginError = null
                },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Icono de Contraseña") },
                trailingIcon = {
                    val image = if (passwordVisible) R.drawable.ic_visibility_on else R.drawable.ic_visibility_off
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = painterResource(id = image), contentDescription = "Mostrar/Ocultar contraseña")
                    }
                }
            )

            loginError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Olvidé mi contraseña",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
                    .clickable {
                        Toast
                            .makeText(context, "Funcionalidad no implementada", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (uiUsername.isNotBlank() && uiPassword.isNotBlank()) {
                        isLoading = true
                        loginError = null
                        coroutineScope.launch {
                            try {
                                val authRequest = AuthRequest(user = uiUsername, password = uiPassword)
                                val authResponse = authService.loginUser(authRequest)
                                if (authResponse != null && authResponse.token.isNotBlank()) {
                                    TokenManager.saveAuthData(context, authResponse.token, uiUsername)
                                    navController.navigate(AppRoutes.homeScreenWithUsername(uiUsername)) {
                                        popUpTo(AppRoutes.LOGIN_ROUTE) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                } else {
                                    loginError = "Usuario o contraseña incorrectos."
                                }
                            } catch (e: Exception) {
                                loginError = "Error de conexión. Intente de nuevo."
                                Log.e("LoginScreen", "Excepción en coroutine de Login", e)
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        loginError = "Por favor, ingrese usuario y contraseña."
                    }
                },
                enabled = !isLoading && !isLoadingGoogle,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Iniciar Sesión", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isLoadingGoogle = true
                    loginError = null
                    googleSignInClient.signOut().addOnCompleteListener {
                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    }
                },
                enabled = !isLoading && !isLoadingGoogle,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF444444)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                if (isLoadingGoogle) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary)
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google logo",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continuar con Google",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { Toast.makeText(context, "Inicio con Facebook no implementado", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1877F2),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Facebook,
                        contentDescription = "Facebook logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Continuar con Facebook", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Regístrate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        Toast
                            .makeText(context, "Funcionalidad no implementada", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AikeTheme {
        LoginScreen(navController = rememberNavController())
    }
}