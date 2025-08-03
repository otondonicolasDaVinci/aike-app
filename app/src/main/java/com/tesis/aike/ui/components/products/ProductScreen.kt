package com.tesis.aike.ui.components.products

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tesis.aike.R
import com.tesis.aike.domain.model.Product
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductScreen(
    rootNavController: NavController,
    username: String,
    productViewModel: ProductViewModel = viewModel()
) {
    val context = LocalContext.current
    val productsByCategory by productViewModel.productsByCategory.collectAsStateWithLifecycle()
    val totalCartQuantity by productViewModel.totalCartQuantity.collectAsStateWithLifecycle()
    val cartItemsList by productViewModel.cartItems.collectAsStateWithLifecycle()
    val totalCartPrice by productViewModel.totalCartPrice.collectAsStateWithLifecycle()
    val isLoadingProducts by productViewModel.isLoadingProducts.collectAsStateWithLifecycle()
    val productErrorMessage by productViewModel.productErrorMessage.collectAsStateWithLifecycle()
    val isCreatingPayment by productViewModel.isCreatingPayment.collectAsStateWithLifecycle()
    val paymentError by productViewModel.paymentError.collectAsStateWithLifecycle()

    var showCartPanel by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        productViewModel.paymentUrl.collect { url ->
            val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            rootNavController.navigate("payment_webview/$encodedUrl")
        }
    }

    LaunchedEffect(paymentError) {
        paymentError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            productViewModel.clearPaymentError()
        }
    }

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
                            IconButton(onClick = { showCartPanel = true }) {
                                Icon(
                                    Icons.Filled.ShoppingCart,
                                    contentDescription = "Carrito de compras"
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isLoadingProducts) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (productErrorMessage != null) {
                    Text(
                        text = productErrorMessage ?: "Error",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
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
                                    items(products) { product ->
                                        val quantityInCart = cartItemsList.find { it.product.id == product.id }?.quantity ?: 0
                                        ProductCard(
                                            product = product,
                                            quantityInCart = quantityInCart,
                                            onAddToCart = { productViewModel.addToCart(it) },
                                            onRemoveFromCart = { productViewModel.removeFromCart(it) }
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
            onUpdateQuantity = { productId, newQuantity ->
                productViewModel.updateQuantityInCartPanel(productId, newQuantity)
            },
            onCheckout = {
                productViewModel.createPaymentPreferenceForCart()
            },
            modifier = Modifier.align(Alignment.CenterEnd)
        )

        if (isCreatingPayment) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
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
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageUrl.takeIf { it != "null" })
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(id = R.drawable.aike_logo),
                error = painterResource(id = R.drawable.aike_logo),
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            )
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
                    modifier = Modifier.heightIn(min = 42.dp)
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
                        Icon(
                            Icons.Filled.RemoveCircleOutline, "Quitar",
                            tint = if (quantityInCart > 0) MaterialTheme.colorScheme.primary else Color.Gray
                        )
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