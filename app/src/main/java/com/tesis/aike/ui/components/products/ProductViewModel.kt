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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val productService = ProductService()
    private val paymentApiService = PaymentApiService()
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



    private val _isCreatingPayment = MutableStateFlow(false)
    val isCreatingPayment: StateFlow<Boolean> = _isCreatingPayment.asStateFlow()

    private val _paymentUrl = MutableSharedFlow<String>()
    val paymentUrl: SharedFlow<String> = _paymentUrl.asSharedFlow()

    private val _paymentError = MutableStateFlow<String?>(null)
    val paymentError: StateFlow<String?> = _paymentError.asStateFlow()

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
                e.printStackTrace()
            } finally {
                _isCreatingPayment.value = false
            }
        }
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

    fun clearPaymentError() {
        _paymentError.value = null
    }
}