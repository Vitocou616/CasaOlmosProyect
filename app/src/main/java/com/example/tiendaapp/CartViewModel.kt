package com.example.tiendaapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tiendaapp.data.AppDatabase
import com.example.tiendaapp.data.CartItem
import com.example.tiendaapp.data.Product
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import com.example.tiendaapp.ui.UiEvent

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val dao = db.cartDao()

    val items: StateFlow<List<CartItem>> = dao.getAll().map { it }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    private var lastRemoved: CartItem? = null

    fun addToCart(product: Product) = viewModelScope.launch {
        val existing = dao.getByProductId(product.id)
        if (existing != null) {
            dao.update(existing.copy(quantity = existing.quantity + 1))
        } else {
            dao.insert(CartItem(productId = product.id, name = product.name, price = product.price, quantity = 1))
        }
        _events.emit(UiEvent("Producto agregado al carrito", actionLabel = null, source = "cart"))
    }

    fun remove(item: CartItem) = viewModelScope.launch {
        // store last removed so we can undo
        lastRemoved = item
        dao.delete(item)
        _events.emit(UiEvent("Producto eliminado", actionLabel = "Deshacer", source = "cart"))
    }

    fun clearCart() = viewModelScope.launch { dao.clear() }

    fun changeQuantity(productId: Int, delta: Int) = viewModelScope.launch {
        val existing = dao.getByProductId(productId)
        if (existing != null) {
            val newQty = existing.quantity + delta
            if (newQty > 0) {
                dao.update(existing.copy(quantity = newQty))
            } else {
                // use remove to set lastRemoved and emit event
                remove(existing)
            }
        }
    }

    fun undoLastRemoved() = viewModelScope.launch {
        lastRemoved?.let {
            dao.insert(it)
            _events.emit(UiEvent("Eliminación deshecha", actionLabel = null, source = "cart"))
            lastRemoved = null
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(application) as T
        }
    }
}
