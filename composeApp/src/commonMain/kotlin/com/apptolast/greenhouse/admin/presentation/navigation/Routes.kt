package com.apptolast.greenhouse.admin.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for the application.
 * Using Kotlin Serialization for type-safe navigation with Compose Navigation.
 */

@Serializable
object LoginRoute

@Serializable
object DashboardRoute

@Serializable
object ClientsRoute

@Serializable
data class ClientDetailRoute(val clientId: String)

@Serializable
object SettingsRoute
