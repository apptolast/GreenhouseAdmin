package com.apptolast.greenhouse.admin.di

import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailViewModel
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientsViewModel
import com.apptolast.greenhouse.admin.presentation.viewmodel.DashboardViewModel
import com.apptolast.greenhouse.admin.presentation.viewmodel.LoginViewModel
import com.apptolast.greenhouse.admin.presentation.viewmodel.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for presentation layer dependencies.
 * Contains ViewModels for the MVVM architecture.
 */
val presentationModule = module {
    // Auth
    viewModelOf(::LoginViewModel)

    // Dashboard
    viewModelOf(::DashboardViewModel)

    // Clients
    viewModelOf(::ClientsViewModel)

    // Client Detail (with clientId parameter)
    viewModel { (clientId: Long) ->
        ClientDetailViewModel(clientId, get(), get(), get(), get(), get(), get(), get(), get())
    }

    // Settings
    viewModelOf(::SettingsViewModel)
}
