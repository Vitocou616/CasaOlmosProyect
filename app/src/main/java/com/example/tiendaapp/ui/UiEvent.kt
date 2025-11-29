package com.example.tiendaapp.ui

/**
 * Simple UI event for snackbars and transient messages.
 * actionLabel: label to show for the action (e.g., "Deshacer"); if null no action.
 * source: optional tag indicating origin ("cart", "auth", "checkout")
 */
data class UiEvent(
    val message: String,
    val actionLabel: String? = null,
    val source: String? = null
)
