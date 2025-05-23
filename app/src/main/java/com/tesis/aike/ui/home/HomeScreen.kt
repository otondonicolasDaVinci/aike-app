package com.tesis.aike.ui.home // Asegúrate que sea tu paquete

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tesis.aike.R
import com.tesis.aike.domain.model.ChatMessage
import com.tesis.aike.ui.theme.AikeTheme

// Define los ítems de la barra de navegación inferior.
sealed class BottomNavItem(val route: String, val icon: ImageVector, val labelForAccessibility: String) {
    object Viking : BottomNavItem("viking_tab", Icons.Filled.Shield, "Principal Aike")
    object Hut : BottomNavItem("hut_tab", Icons.Filled.Home, "Hogar")
    object Key : BottomNavItem("key_tab", Icons.Filled.Key, "Claves")
    object Bag : BottomNavItem("bag_tab", Icons.Filled.ShoppingBag, "Bolsa")
    object Profile : BottomNavItem("profile_tab", Icons.Filled.Person, "Perfil")
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier, // El modifier que viene como parámetro a HomeScreen
    navController: NavController,
    username: String
) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
    val bottomNavItems = listOf(
        BottomNavItem.Viking,
        BottomNavItem.Hut,
        BottomNavItem.Key,
        BottomNavItem.Bag,
        BottomNavItem.Profile
    )
    var showChatUi by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF2C2C2C)
            ) {
                // ... (tu NavigationBarItems) ...
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            println("${item.labelForAccessibility} clicked")
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.labelForAccessibility,
                                tint = if (selectedItemIndex == index) MaterialTheme.colorScheme.primary else Color.LightGray
                            )
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier // Quitamos el modifier del parámetro aquí, lo usaremos dentro
                .fillMaxSize()
                // 1. El Box ahora usa el color de fondo del tema por defecto
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .clickable(
                    enabled = !showChatUi,
                    onClick = { showChatUi = true }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!showChatUi) {
                // --- CONTENIDO DE BIENVENIDA ---
                Column(
                    // 2. La Column de bienvenida AHORA establece su propio fondo oscuro
                    modifier = Modifier // Usamos un Modifier nuevo para esta Column
                        .fillMaxSize()
                        .background(Color(0xFF404040)) // Fondo oscuro específico para la bienvenida
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.aike_logo),
                        contentDescription = "Logo Aike Asistente Vikingo",
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .aspectRatio(1f)
                            .padding(bottom = 16.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "¡Hola $username!\nSaludos desde la Patagonia argentina, Soy Aike, tu asistente, y hoy te explicare como usar esta aplicación",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 26.sp
                        ),
                        fontWeight = FontWeight.Medium
                    )
                }
                // --- FIN CONTENIDO DE BIENVENIDA ---
            } else {
                // --- INTERFAZ DEL CHAT ---
                // ChatInterface se mostrará sobre el fondo predeterminado del Box (MaterialTheme.colorScheme.background)
                ChatInterface(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun ChatInterface(modifier: Modifier = Modifier) {
    // Obtén una instancia del ChatViewModel
    val viewModel: ChatViewModel = viewModel()

    // Observa los StateFlows del ViewModel
    val messages by viewModel.messages.collectAsStateWithLifecycle() // Lista de mensajes
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle() // Estado de carga
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle() // Mensaje de error

    var messageText by rememberSaveable { mutableStateOf("") } // El texto del input se mantiene aquí por simplicidad

    Column(modifier = modifier.fillMaxSize()) {
        // 1. Área de mensajes
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            reverseLayout = true // Mantiene los mensajes nuevos abajo
        ) {
            // Itera sobre la lista de mensajes del ViewModel
            // Usamos .reversed() porque reverseLayout=true en LazyColumn espera que la lista original
            // tenga los más nuevos al final para que aparezcan abajo.
            // Si tu ViewModel ya añade los nuevos al final, entonces .reversed() aquí es correcto.
            items(messages.reversed()) { chatMessage ->
                MessageBubble(chatMessage = chatMessage) // Usamos un Composable para cada burbuja de mensaje
            }
        }

        // Indicador de carga
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
            )
        }

        // Mensaje de error
        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                textAlign = TextAlign.Center
            )
            // Podrías añadir un botón para descartar el error o reintentar
            // Button(onClick = { viewModel.clearErrorMessage() }) { Text("Descartar") }
        }

        // 2. Área de entrada de texto y botón de enviar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = {
                    Text("Escribe un mensaje...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                singleLine = true,
                enabled = !isLoading // Deshabilitar input mientras carga
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(messageText) // Llama al ViewModel para enviar el mensaje
                        messageText = "" // Limpiar el campo de texto
                    }
                },
                enabled = !isLoading // Deshabilitar botón mientras carga
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar mensaje",
                    tint = if (isLoading) Color.Gray else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// Nuevo Composable para la burbuja del mensaje (puedes personalizarlo mucho más)
@Composable
fun MessageBubble(chatMessage: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        // Alinea los mensajes del usuario a la derecha, los de la IA a la izquierda
        horizontalArrangement = if (chatMessage.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            // Cambia el color de la burbuja según quién envía el mensaje
            colors = CardDefaults.cardColors(
                containerColor = if (chatMessage.isFromUser) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.widthIn(max = 300.dp) // Ancho máximo para las burbujas
        ) {
            Text(
                text = chatMessage.text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                color = if (chatMessage.isFromUser) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=740dp")
@Composable
fun HomeScreenPreview() {
    AikeTheme {
        HomeScreen(navController = rememberNavController(), username = "UsuarioDePrueba")
    }
}