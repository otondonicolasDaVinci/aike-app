package com.tesis.aike.ui.components.products

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tesis.aike.data.remote.api.PaymentApiService
import com.tesis.aike.data.remote.api.ProductService
import com.tesis.aike.data.remote.dto.CartItemRequest
import com.tesis.aike.data.remote.dto.CartPaymentRequest
import com.tesis.aike.domain.model.CartItem
import com.tesis.aike.domain.model.Product
import com.tesis.aike.util.TokenManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val productService = ProductService()
    private val paymentApiService = PaymentApiService()
    private val appContext = application.applicationContext

    private val _productsByCategory = MutableStateFlow<Map<String, List<Product>>>(emptyMap())
    val productsByCategory: StateFlow<Map<String, List<Product>>> = _productsByCategory

    private val _cartItemsMap = MutableStateFlow<Map<Long, CartItem>>(emptyMap())
    val cartItems: StateFlow<List<CartItem>> = _cartItemsMap.map { it.values.toList() }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val totalCartQuantity: StateFlow<Int> = _cartItemsMap.map { it.values.sumOf { item -> item.quantity } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val totalCartPrice: StateFlow<Double> = _cartItemsMap.map { it.values.sumOf { item -> item.product.price * item.quantity } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _isLoadingProducts = MutableStateFlow(false)
    val isLoadingProducts: StateFlow<Boolean> = _isLoadingProducts

    private val _productErrorMessage = MutableStateFlow<String?>(null)
    val productErrorMessage: StateFlow<String?> = _productErrorMessage

    private val _isCreatingPayment = MutableStateFlow(false)
    val isCreatingPayment: StateFlow<Boolean> = _isCreatingPayment

    private val _paymentUrl = MutableSharedFlow<String>()
    val paymentUrl: SharedFlow<String> = _paymentUrl

    private val _paymentError = MutableStateFlow<String?>(null)
    val paymentError: StateFlow<String?> = _paymentError

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        if (_isLoadingProducts.value) return
        viewModelScope.launch {
            _isLoadingProducts.value = true
            val token = TokenManager.getToken(appContext)
            try {
                val fetchedProducts = productService.getAllProducts(token)
                _productsByCategory.value = fetchedProducts?.groupBy { it.category } ?: emptyMap()
            } catch (e: Exception) {
                _productErrorMessage.value = "Error al cargar productos."
            } finally {
                _isLoadingProducts.value = false
            }
        }
    }

    fun createPaymentPreferenceForCart() {
        if (_isCreatingPayment.value) return
        viewModelScope.launch {
            _isCreatingPayment.value = true
            _paymentError.value = null
            val currentCartItems = _cartItemsMap.value.values.toList()
            if (currentCartItems.isEmpty()) {
                _paymentError.value = "El carrito está vacío."
                _isCreatingPayment.value = false
                return@launch
            }

            val requestItems = currentCartItems.map { cartItem ->
                CartItemRequest(
                    productId = cartItem.product.id,
                    quantity = cartItem.quantity
                )
            }

            val userId = TokenManager.getUserId(appContext)?.toLongOrNull()
            val request = CartPaymentRequest(items = requestItems, userId = userId, payerEmail = null)
            val token = TokenManager.getToken(appContext)

            try {
                val paymentResponse = paymentApiService.createPaymentPreference(token, request)
                if (paymentResponse != null) {
                    _paymentUrl.emit(paymentResponse.detail)
                } else {
                    _paymentError.value = "No se pudo crear la preferencia de pago."
                }
            } catch (e: Exception) {
                _paymentError.value = "Error de conexión: ${e.message}"
            } finally {
                _isCreatingPayment.value = false
            }
        }
    }

    fun addToCart(product: Product) {
        _cartItemsMap.update { currentCart ->
            val mutableCart = currentCart.toMutableMap()
            val cartItem = mutableCart[product.id]
            mutableCart[product.id] = cartItem?.copy(quantity = cartItem.quantity + 1) ?: CartItem(product = product, quantity = 1)
            mutableCart
        }
    }

    fun removeFromCart(product: Product) {
        _cartItemsMap.update { currentCart ->
            val mutableCart = currentCart.toMutableMap()
            val cartItem = mutableCart[product.id]
            if (cartItem != null && cartItem.quantity > 1) {
                mutableCart[product.id] = cartItem.copy(quantity = cartItem.quantity - 1)
            } else {
                mutableCart.remove(product.id)
            }
            mutableCart
        }
    }

    fun updateQuantityInCartPanel(productId: Long, newQuantity: Int) {
        _cartItemsMap.update { currentCart ->
            val mutableCart = currentCart.toMutableMap()
            if (mutableCart.containsKey(productId)) {
                if (newQuantity > 0) {
                    mutableCart[productId] = mutableCart[productId]!!.copy(quantity = newQuantity)
                } else {
                    mutableCart.remove(productId)
                }
            }
            mutableCart
        }
    }

    fun clearPaymentError() {
        _paymentError.value = null
    }
}