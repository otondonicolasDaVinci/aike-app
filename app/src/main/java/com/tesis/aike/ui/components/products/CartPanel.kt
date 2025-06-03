package com.tesis.aike.ui.components.products

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tesis.aike.domain.model.CartItem
import com.tesis.aike.domain.model.Product
import com.tesis.aike.ui.theme.AikeTheme
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.tesis.aike.R


@Composable
fun CartPanel(
    isVisible: Boolean,
    cartItems: List<CartItem>,
    onDismiss: () -> Unit,
    onUpdateQuantity: (productId: String, newQuantity: Int) -> Unit,
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
            HorizontalDivider() // CAMBIO AQUÍ

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
                    items(cartItems) { cartItem ->
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

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEEF))
            ) {
                Text("Pagar por Mercado pago", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItem,
    currencyFormatter: NumberFormat,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.LightGray, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(id = R.drawable.aike_logo), // Usa un placeholder o cartItem.product.imageUrl
                contentDescription = cartItem.product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(cartItem.product.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(currencyFormatter.format(cartItem.product.price), fontSize = 12.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onQuantityChange(cartItem.quantity - 1) }, Modifier.size(30.dp)) {
                Icon(Icons.Filled.Remove, "Quitar", tint = MaterialTheme.colorScheme.primary)
            }
            Text(cartItem.quantity.toString(), modifier = Modifier.padding(horizontal = 4.dp))
            IconButton(onClick = { onQuantityChange(cartItem.quantity + 1) }, Modifier.size(30.dp)) {
                Icon(Icons.Filled.Add, "Agregar", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Preview
@Composable
fun CartPanelPreview() {
    AikeTheme {
        val sampleProducts = listOf(
            Product("1", "Mermelada de Frutilla", 1500.0, "...", R.drawable.aike_logo.toString(), "Mermelada"),
            Product("2", "Chocolate Amargo", 2500.0, "...", R.drawable.aike_logo.toString(), "Chocolates")
        )
        val sampleCartItems = listOf(
            CartItem(sampleProducts[0], 2),
            CartItem(sampleProducts[1], 1)
        )
        CartPanel(
            isVisible = true,
            cartItems = sampleCartItems,
            onDismiss = {},
            onUpdateQuantity = { _, _ -> },
            onCheckout = {}
        )
    }
}

@Preview
@Composable
fun CartPanelEmptyPreview() {
    AikeTheme {
        CartPanel(
            isVisible = true,
            cartItems = emptyList(),
            onDismiss = {},
            onUpdateQuantity = { _, _ -> },
            onCheckout = {}
        )
    }
}