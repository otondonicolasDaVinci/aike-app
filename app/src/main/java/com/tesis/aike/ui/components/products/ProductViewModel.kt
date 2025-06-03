package com.tesis.aike.ui.components.products

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tesis.aike.R
import com.tesis.aike.domain.model.CartItem
import com.tesis.aike.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ProductViewModel : ViewModel() {

    private val _productsByCategory = MutableStateFlow<Map<String, List<Product>>>(emptyMap())
    val productsByCategory: StateFlow<Map<String, List<Product>>> = _productsByCategory.asStateFlow()

    private val _cartItemsMap = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartItems: StateFlow<List<CartItem>> = _cartItemsMap
        .map { it.values.toList().sortedBy { item -> item.product.title } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val totalCartQuantity: StateFlow<Int> = _cartItemsMap
        .map { map -> map.values.sumOf { it.quantity } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0
        )

    val totalCartPrice: StateFlow<Double> = _cartItemsMap
        .map { map -> map.values.sumOf { it.product.price * it.quantity } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0.0
        )

    init {
        loadSampleProducts()
    }

    private fun loadSampleProducts() {
        _productsByCategory.value = mapOf(
            "Mermelada" to listOf(
                Product("1", "Mermelada de Frutilla", 1500.0, "Lorem ipsum es el texto que se usa habitualmente en diseño gráfico.", R.drawable.aike_logo.toString(), "Mermelada"),
                Product("2", "Mermelada de Frambuesa", 1600.0, "Exquisita mermelada de frambuesas frescas.", R.drawable.aike_logo.toString(), "Mermelada"),
                Product("3", "Mermelada de Sauco", 1700.0, "Mermelada única de sauco patagónico.", R.drawable.aike_logo.toString(), "Mermelada")
            ),
            "Chocolates" to listOf(
                Product("4", "Chocolate Amargo 70%", 2500.0, "Intenso chocolate amargo con 70% cacao.", R.drawable.aike_logo.toString(), "Chocolates"),
                Product("5", "Chocolate con Leche", 2200.0, "Suave chocolate con leche y almendras.", R.drawable.aike_logo.toString(), "Chocolates")
            )
        )
    }

    fun getQuantityInCart(productId: String): Int {
        return _cartItemsMap.value[productId]?.quantity ?: 0
    }

    fun addToCart(product: Product) {
        _cartItemsMap.update { currentCart ->
            val mutableCart = currentCart.toMutableMap()
            val cartItem = mutableCart[product.id]
            if (cartItem != null) {
                mutableCart[product.id] = cartItem.copy(quantity = cartItem.quantity + 1)
            } else {
                mutableCart[product.id] = CartItem(product = product, quantity = 1)
            }
            Log.d("ProductViewModel", "Cart updated: $mutableCart, Total Qty: ${mutableCart.values.sumOf { it.quantity }}")
            mutableCart
        }
    }

    fun removeFromCart(product: Product) {
        _cartItemsMap.update { currentCart ->
            val mutableCart = currentCart.toMutableMap()
            val cartItem = mutableCart[product.id]
            if (cartItem != null) {
                if (cartItem.quantity > 1) {
                    mutableCart[product.id] = cartItem.copy(quantity = cartItem.quantity - 1)
                } else {
                    mutableCart.remove(product.id)
                }
            }
            Log.d("ProductViewModel", "Cart updated: $mutableCart, Total Qty: ${mutableCart.values.sumOf { it.quantity }}")
            mutableCart
        }
    }

    fun updateQuantityInCartPanel(productId: String, newQuantity: Int) {
        _cartItemsMap.update { currentCart ->
            val mutableCart = currentCart.toMutableMap()
            val cartItem = mutableCart[productId]
            if (cartItem != null) {
                if (newQuantity > 0) {
                    mutableCart[productId] = cartItem.copy(quantity = newQuantity)
                } else {
                    mutableCart.remove(productId)
                }
            }
            mutableCart
        }
    }
}