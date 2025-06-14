package com.tesis.aike.ui.components.products

import android.app.Application 
import android.util.Log
import androidx.lifecycle.AndroidViewModel 
import androidx.lifecycle.viewModelScope
import com.tesis.aike.data.remote.api.ProductService 
import com.tesis.aike.domain.model.CartItem
import com.tesis.aike.domain.model.Product
import com.tesis.aike.util.TokenManager 
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) { 

    private val productService = ProductService() 
    private val appContext = application.applicationContext 

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

    private val _isLoadingProducts = MutableStateFlow(false)
    val isLoadingProducts: StateFlow<Boolean> = _isLoadingProducts.asStateFlow()

    private val _productErrorMessage = MutableStateFlow<String?>(null)
    val productErrorMessage: StateFlow<String?> = _productErrorMessage.asStateFlow()


    init {
        fetchProducts() 
    }

    fun fetchProducts() {
        if (_isLoadingProducts.value) return

        viewModelScope.launch {
            _isLoadingProducts.value = true
            _productErrorMessage.value = null
            
            
            
            val token = TokenManager.getToken(appContext)

            try {
                val fetchedProducts = productService.getAllProducts(token)
                if (fetchedProducts != null) {
                    
                    _productsByCategory.value = fetchedProducts.groupBy { it.category }
                } else {
                    _productErrorMessage.value = "No se pudieron cargar los productos."
                }
            } catch (e: Exception) {
                _productErrorMessage.value = "Error al cargar productos: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoadingProducts.value = false
            }
        }
    }


    fun getQuantityInCart(productId: Long): Int { 
        return _cartItemsMap.value[productId.toString()]?.quantity ?: 0 
    }

    fun addToCart(product: Product) {
        _cartItemsMap.update { currentCart ->
            val mutableCart = currentCart.toMutableMap()
            val cartItem = mutableCart[product.id.toString()] 
            if (cartItem != null) {
                mutableCart[product.id.toString()] = cartItem.copy(quantity = cartItem.quantity + 1)
            } else {
                mutableCart[product.id.toString()] = CartItem(product = product, quantity = 1)
            }
            Log.d("ProductViewModel", "Cart updated: $mutableCart, Total Qty: ${mutableCart.values.sumOf { it.quantity }}")
            mutableCart
        }
    }

    fun removeFromCart(product: Product) {
        _cartItemsMap.update { currentCart ->
            val mutableCart = currentCart.toMutableMap()
            val cartItem = mutableCart[product.id.toString()]
            if (cartItem != null) {
                if (cartItem.quantity > 1) {
                    mutableCart[product.id.toString()] = cartItem.copy(quantity = cartItem.quantity - 1)
                } else {
                    mutableCart.remove(product.id.toString())
                }
            }
            Log.d("ProductViewModel", "Cart updated: $mutableCart, Total Qty: ${mutableCart.values.sumOf { it.quantity }}")
            mutableCart
        }
    }

    fun updateQuantityInCartPanel(productId: Long, newQuantity: Int) { 
        _cartItemsMap.update { currentCart ->
            val mutableCart = currentCart.toMutableMap()
            val cartItem = mutableCart[productId.toString()]
            if (cartItem != null) {
                if (newQuantity > 0) {
                    mutableCart[productId.toString()] = cartItem.copy(quantity = newQuantity)
                } else {
                    mutableCart.remove(productId.toString())
                }
            }
            mutableCart
        }
    }
}