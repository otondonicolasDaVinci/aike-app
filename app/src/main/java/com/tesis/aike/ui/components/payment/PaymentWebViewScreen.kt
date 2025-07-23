package com.tesis.aike.ui.components.payment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.tesis.aike.AppRoutes

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PaymentWebViewScreen(
    navController: NavController,
    paymentUrl: String
) {
    var isLoading by remember { mutableStateOf(true) }

    val successUrl = "https://ymucpmxkp3.us-east-1.awsapprunner.com/api/payments/success"
    val failureUrl = "https://ymucpmxkp3.us-east-1.awsapprunner.com/api/payments/failure"
    val pendingUrl = "https://ymucpmxkp3.us-east-1.awsapprunner.com/api/payments/pending"

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        isLoading = true
                        super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        isLoading = false
                        when (url) {
                            successUrl -> {
                                navController.navigate(AppRoutes.PAYMENT_SUCCESS_ROUTE) {
                                    popUpTo(AppRoutes.MAIN_APP_ROUTE) { inclusive = false }
                                }
                            }
                            failureUrl, pendingUrl -> {
                                navController.navigate(AppRoutes.PAYMENT_FAILURE_ROUTE) {
                                    popUpTo(AppRoutes.MAIN_APP_ROUTE) { inclusive = false }
                                }
                            }
                        }
                        super.onPageFinished(view, url)
                    }
                }
                loadUrl(paymentUrl)
            }
        }, modifier = Modifier.fillMaxSize())

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    BackHandler {
        navController.popBackStack()
    }
}