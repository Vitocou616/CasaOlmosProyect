package com.example.tiendaapp

import android.content.Intent
import android.os.Bundle
// vibration removed: not needed
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tiendaapp.data.AppDatabase
import com.example.tiendaapp.data.Product
import com.example.tiendaapp.data.ProductRepository
import com.example.tiendaapp.ui.screens.*
import com.example.tiendaapp.ui.theme.TiendaAppTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: CartViewModel by viewModels {
        CartViewModel.Factory((applicationContext as android.app.Application))
    }
    private val authViewModel: com.example.tiendaapp.auth.AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize DB in background (Room will create DB file)
        AppDatabase.getInstance(applicationContext)

        setContent {
            TiendaAppTheme {
                // Scaffold provides slots for topBar, bottomBar and a SnackbarHost.
                val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
                Scaffold(snackbarHost = { androidx.compose.material3.SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
                    Surface(modifier = Modifier.fillMaxSize().padding(innerPadding), color = MaterialTheme.colorScheme.background) {
                        val navController = rememberNavController()
                        val db = AppDatabase.getInstance(this@MainActivity)
                        val productFlow = db.productDao().getAll()
                        val products by productFlow.collectAsState(initial = emptyList())

                        // Collect events from Cart and Auth ViewModels and show snackbars
                        val scope = rememberCoroutineScope()
                        LaunchedEffect(Unit) {
                            // Cart events
                            viewModel.events.collect { ev ->
                                val result = snackbarHostState.showSnackbar(ev.message, ev.actionLabel)
                                if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                                    // if undo requested, call undo
                                    if (ev.source == "cart") {
                                        viewModel.undoLastRemoved()
                                    }
                                }
                            }
                        }

                        LaunchedEffect(Unit) {
                            authViewModel.events.collect { ev ->
                                snackbarHostState.showSnackbar(ev.message, ev.actionLabel)
                            }
                        }

                        NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                products = products,
                                onProductClick = { id: Int -> navController.navigate("product/$id") },
                                onOpenCart = { navController.navigate("cart") },
                                onAddToCart = { product: Product -> viewModel.addToCart(product) },
                                onRegister = { navController.navigate("register") },
                                onLogin = { navController.navigate("login") },
                                onExplore = { navController.navigate("explore") },
                                isLoggedIn = (authViewModel.currentUserId.collectAsState().value > 0L),
                                onOpenAccount = { navController.navigate("account") }
                            )
                        }
                        composable("product/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStack ->
                            val id = backStack.arguments?.getInt("id") ?: 0
                            val product = products.find { it.id == id }
                            product?.let {
                                ProductDetailScreen(product = it, onAdd = { viewModel.addToCart(it) }, onBack = { navController.popBackStack() })
                            }
                        }
                        composable("cart") {
                            CartScreen(
                                viewModel = viewModel,
                                onCheckout = { navController.navigate("checkout") }
                            )
                        }
                        composable("checkout") {
                            val uid = authViewModel.currentUserId.collectAsState().value
                            val itemsSnapshot = viewModel.items.collectAsState().value
                            CheckoutScreen(onFinish = { info: String ->
                                // Persist order in backend AND local DB
                                val total = itemsSnapshot.sumOf { it.price * it.quantity }
                                val currentUserId = if (uid > 0) uid else 0L
                                
                                lifecycleScope.launch {
                                    try {
                                        // 1. Enviar al backend
                                        val createRequest = com.example.tiendaapp.network.model.CreateOrderRequest(
                                            userId = currentUserId,
                                            total = total,
                                            createdAt = System.currentTimeMillis(),
                                            items = itemsSnapshot.map {
                                                com.example.tiendaapp.network.model.CreateOrderItemRequest(
                                                    productName = it.name,
                                                    price = it.price,
                                                    quantity = it.quantity
                                                )
                                            }
                                        )
                                        val response = com.example.tiendaapp.network.RetrofitClient.orderApi.createOrder(createRequest)
                                        
                                        // 2. Guardar localmente (fallback y cache)
                                        val orderDao = AppDatabase.getInstance(this@MainActivity).orderDao()
                                        val orderId = orderDao.insertOrder(com.example.tiendaapp.data.Order(
                                            userId = currentUserId,
                                            total = total,
                                            createdAt = System.currentTimeMillis()
                                        ))
                                        val orderItems = itemsSnapshot.map {
                                            com.example.tiendaapp.data.OrderItem(
                                                orderId = orderId,
                                                productName = it.name,
                                                price = it.price,
                                                quantity = it.quantity
                                            )
                                        }
                                        orderDao.insertItems(orderItems)
                                        
                                        if (response.isSuccessful) {
                                            snackbarHostState.showSnackbar("✓ Orden guardada en servidor")
                                        } else {
                                            snackbarHostState.showSnackbar("✓ Orden guardada localmente (servidor no disponible)")
                                        }
                                    } catch (e: Exception) {
                                        // Fallback: solo guardar local si falla el backend
                                        val orderDao = AppDatabase.getInstance(this@MainActivity).orderDao()
                                        val orderId = orderDao.insertOrder(com.example.tiendaapp.data.Order(
                                            userId = currentUserId,
                                            total = total,
                                            createdAt = System.currentTimeMillis()
                                        ))
                                        val orderItems = itemsSnapshot.map {
                                            com.example.tiendaapp.data.OrderItem(
                                                orderId = orderId,
                                                productName = it.name,
                                                price = it.price,
                                                quantity = it.quantity
                                            )
                                        }
                                        orderDao.insertItems(orderItems)
                                        snackbarHostState.showSnackbar("✓ Orden guardada localmente")
                                    }
                                }

                                // share summary via native intent
                                val send = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, info as CharSequence)
                                    type = "text/plain"
                                }
                                startActivity(Intent.createChooser(send, "Compartir recibo"))
                                // clear cart
                                viewModel.clearCart()
                                // show a snackbar confirming purchase
                                scope.launch {
                                    snackbarHostState.showSnackbar("Compra finalizada")
                                }
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = false }
                                }
                            })
                        }
                        composable("register") {
                            RegisterScreen(onRegister = { name: String, email: String, password: String ->
                                // Use AuthViewModel to register (handles hashing and session)
                                authViewModel.register(name, email, password.toCharArray()) { res ->
                                    if (res.isSuccess) {
                                        navController.popBackStack()
                                    } else {
                                        // Could surface error via Snackbar; left as TODO
                                    }
                                }
                            }, onBack = { navController.popBackStack() })
                        }
                        composable("login") {
                            LoginScreen(onLogin = {
                                authViewModel.refreshCurrentUser()
                                navController.popBackStack()
                            }, onBack = { navController.popBackStack() })
                        }
                        composable("explore") {
                            ExploreScreen(
                                onBack = { navController.popBackStack() },
                                onAddToCart = { product ->
                                    viewModel.addToCart(product)
                                }
                            )
                        }
                        composable("account") {
                            val uid = authViewModel.currentUserId.collectAsState().value
                            AccountScreen(
                                userId = uid,
                                userEmail = null, // could be extended to show email from repository
                                cartViewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
            }
        }
    }
}
