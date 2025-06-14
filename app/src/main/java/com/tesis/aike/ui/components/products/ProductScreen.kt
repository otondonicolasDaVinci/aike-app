package com.tesis.aike.ui.components.products

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tesis.aike.AppRoutes
import com.tesis.aike.R
import com.tesis.aike.domain.model.CartItem
import com.tesis.aike.domain.model.Product
import com.tesis.aike.ui.home.AppBottomNavigationBar
import com.tesis.aike.ui.home.BottomNavItem
import com.tesis.aike.ui.theme.AikeTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductScreen(
    navController: NavController,
    username: String,
    productViewModel: ProductViewModel = viewModel()
) {
    val bottomNavItems = listOf(
        BottomNavItem.Viking,
        BottomNavItem.Hut,
        BottomNavItem.Key,
        BottomNavItem.Bag,
        BottomNavItem.Profile
    )
    val productsByCategory by productViewModel.productsByCategory.collectAsStateWithLifecycle()
    val totalCartQuantity by productViewModel.totalCartQuantity.collectAsStateWithLifecycle()
    val cartItemsList by productViewModel.cartItems.collectAsStateWithLifecycle()
    val totalCartPrice by productViewModel.totalCartPrice.collectAsStateWithLifecycle()
    val isLoadingProducts by productViewModel.isLoadingProducts.collectAsStateWithLifecycle()
    val productErrorMessage by productViewModel.productErrorMessage.collectAsStateWithLifecycle()

    var showCartPanel by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Productos", fontWeight = FontWeight.Bold) },
                    actions = {
                        BadgedBox(
                            badge = {
                                if (totalCartQuantity > 0) {
                                    Badge { Text(totalCartQuantity.toString()) }
                                }
                            }
                        ) {
                            IconButton(onClick = {
                                showCartPanel = true
                            }) {
                                Icon(
                                    Icons.Filled.ShoppingCart,
                                    contentDescription = "Carrito de compras"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                AppBottomNavigationBar(
                    navController = navController,
                    items = bottomNavItems,
                    currentUsername = username,
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
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                if (isLoadingProducts) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (productErrorMessage != null) {
                    Text(
                        text = productErrorMessage ?: "Error cargando productos",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                } else if (productsByCategory.isEmpty()){
                    Text(
                        text = "No hay productos disponibles en este momento.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        productsByCategory.forEach { (category, products) ->
                            item {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                )
                            }
                            item {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(bottom = 16.dp)
                                ) {
                                    items(products) { product: Product ->
                                        val quantityInCart = cartItemsList.find { cartItem: CartItem -> cartItem.product.id == product.id }?.quantity ?: 0
                                        ProductCard(
                                            product = product,
                                            quantityInCart = quantityInCart,
                                            onAddToCart = { p: Product -> productViewModel.addToCart(p) },
                                            onRemoveFromCart = { p: Product -> productViewModel.removeFromCart(p) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        CartPanel(
            isVisible = showCartPanel,
            cartItems = cartItemsList,
            totalPrice = totalCartPrice,
            onDismiss = { showCartPanel = false },
            onUpdateQuantity = { productId: Long, newQuantity: Int ->
                productViewModel.updateQuantityInCartPanel(productId, newQuantity)
            },
            onCheckout = {
                Log.d("ProductScreen", "Checkout clicked!")
            },
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    quantityInCart: Int,
    onAddToCart: (Product) -> Unit,
    onRemoveFromCart: (Product) -> Unit
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "AR")) }

    Card(
        modifier = Modifier.width(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(id = R.drawable.aike_logo),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(Modifier.padding(12.dp)) {
                Text(product.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 16.sp)
                Text(currencyFormatter.format(product.price), fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Text(
                    product.description,
                    fontSize = 12.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.heightIn(min = (14 * 3).dp)
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { onRemoveFromCart(product) },
                        enabled = quantityInCart > 0,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Filled.RemoveCircleOutline, "Quitar",
                            tint = if (quantityInCart > 0) MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                    Text(quantityInCart.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(
                        onClick = { onAddToCart(product) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Filled.AddCircleOutline, "Agregar", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun CartPanel(
    isVisible: Boolean,
    cartItems: List<CartItem>,
    totalPrice: Double,
    onDismiss: () -> Unit,
    onUpdateQuantity: (productId: Long, newQuantity: Int) -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "AR")) }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }),
        exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }),
        modifier = modifier
            .fillMaxHeight()
            .widthIn(max = 320.dp)
            .shadow(8.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tu carrito", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar carrito")
                }
            }
            HorizontalDivider()

            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    items(cartItems) { cartItem: CartItem ->
                        CartItemRow(
                            cartItem = cartItem,
                            currencyFormatter = currencyFormatter,
                            onQuantityChange = { newQuantity: Int ->
                                onUpdateQuantity(cartItem.product.id, newQuantity)
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }

            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(currencyFormatter.format(totalPrice), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEEF))
            ) {
                Text("Pagar por Mercado pago", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductScreenPreview() {
    AikeTheme {
        ProductScreen(navController = rememberNavController(), username = "TestUser")
    }
}

@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    AikeTheme {
        ProductCard(
            product = Product(id = 1L, title = "Mermelada de Frutilla", description = "Deliciosa mermelada artesanal.", price = 1500.0, imageUrl = null, category = "Mermelada"),
            quantityInCart = 1,
            onAddToCart = {},
            onRemoveFromCart = {}
        )
    }
}

@Preview
@Composable
fun CartPanelFilledPreview() {
    AikeTheme {
        val sampleProducts = listOf(
            Product(id = 1L, title = "Mermelada de Frutilla", description = "Desc. Frutilla", price = 1500.0, imageUrl = null, category = "Mermelada"),
            Product(id = 2L, title = "Chocolate Amargo", description = "Desc. Chocolate", price = 2500.0, imageUrl = null, category = "Chocolates")
        )
        val sampleCartItems = listOf(
            CartItem(sampleProducts[0], 2),
            CartItem(sampleProducts[1], 1)
        )
        CartPanel(
            isVisible = true,
            cartItems = sampleCartItems,
            totalPrice = sampleCartItems.sumOf { it.product.price * it.quantity },
            onDismiss = {},
            onUpdateQuantity = { _, _ -> },
            onCheckout = {}
        )
    }
}