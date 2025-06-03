package com.tesis.aike.ui.home

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tesis.aike.AppRoutes
import com.tesis.aike.R
import com.tesis.aike.domain.model.ChatMessage
import com.tesis.aike.ui.theme.AikeTheme

sealed class BottomNavItem(val routeBase: String, val icon: ImageVector, val labelForAccessibility: String) {
    object Viking : BottomNavItem(AppRoutes.HOME_BASE, Icons.Filled.Shield, "Principal Aike")
    object Hut : BottomNavItem(AppRoutes.RESERVATION_BASE, Icons.Filled.Home, "Mi Reserva")
    object Key : BottomNavItem(AppRoutes.QR_CODE_BASE, Icons.Filled.Key, "Claves QR")
    object Bag : BottomNavItem(AppRoutes.PRODUCTS_BASE, Icons.Filled.ShoppingBag, "Productos")
    object Profile : BottomNavItem(AppRoutes.PROFILE_BASE, Icons.Filled.Person, "Perfil")
}

@Composable
fun AppBottomNavigationBar(
    navController: NavController,
    items: List<BottomNavItem>,
    currentUsername: String,
    onVikingTabAlreadyHome: () -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF2C2C2C)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            val destinationRouteWithArg = when (item) {
                BottomNavItem.Viking -> AppRoutes.homeScreenWithUsername(currentUsername)
                BottomNavItem.Hut -> AppRoutes.reservationScreenWithUsername(currentUsername)
                BottomNavItem.Key -> AppRoutes.qrCodeScreenWithUsername(currentUsername)
                BottomNavItem.Profile -> AppRoutes.profileScreenWithUsername(currentUsername)
                BottomNavItem.Bag -> AppRoutes.productsScreenWithUsername(currentUsername)
                else -> item.routeBase
            }

            val isSelected = currentDestination?.hierarchy?.any { it.route == destinationRouteWithArg } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentDestination?.route == destinationRouteWithArg) {
                        if (item == BottomNavItem.Viking) {
                            onVikingTabAlreadyHome()
                        }
                    } else if (!destinationRouteWithArg.contains("placeholder")) {
                        navController.navigate(destinationRouteWithArg) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        Log.d("AppBottomNav", "${item.labelForAccessibility} (${item.routeBase}) clicked - Implementar navegación")
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.labelForAccessibility,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
                    )
                },
                alwaysShowLabel = false
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    username: String
) {
    val bottomNavItems = listOf(
        BottomNavItem.Viking,
        BottomNavItem.Hut,
        BottomNavItem.Key,
        BottomNavItem.Bag,
        BottomNavItem.Profile
    )
    val chatViewModel: ChatViewModel = viewModel()
    val initialWelcomePending by chatViewModel.initialWelcomePending.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                navController = navController,
                items = bottomNavItems,
                currentUsername = username,
                onVikingTabAlreadyHome = {
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .clickable(
                    enabled = initialWelcomePending,
                    onClick = {
                        if (initialWelcomePending) {
                            chatViewModel.markInitialWelcomeAsShown()
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (initialWelcomePending) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF404040))
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
            } else {
                ChatInterface(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun ChatInterface(modifier: Modifier = Modifier) {
    val viewModel: ChatViewModel = viewModel()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val messageInputText by viewModel.messageInputText.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { chatMessage ->
                MessageBubble(chatMessage = chatMessage)
            }
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
            )
        }
        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageInputText,
                onValueChange = { viewModel.onMessageInputChanged(it) },
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
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    viewModel.sendMessage()
                },
                enabled = !isLoading && messageInputText.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar mensaje",
                    tint = if (isLoading || messageInputText.isBlank()) Color.Gray else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun MessageBubble(chatMessage: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (chatMessage.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (chatMessage.isFromUser) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.widthIn(max = 300.dp)
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