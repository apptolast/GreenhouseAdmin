package com.apptolast.greenhouse.admin.di

import com.apptolast.greenhouse.admin.presentation.viewmodel.DashboardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for presentation layer dependencies.
 * Contains ViewModels for the MVVM architecture.
 */
val presentationModule = module {
    // Dashboard
    viewModelOf(::DashboardViewModel)
}
