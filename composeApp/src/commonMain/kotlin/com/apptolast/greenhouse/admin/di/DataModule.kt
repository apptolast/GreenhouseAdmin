package com.apptolast.greenhouse.admin.di

import com.apptolast.greenhouse.admin.data.repository.DashboardRepositoryImpl
import com.apptolast.greenhouse.admin.domain.repository.DashboardRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for data layer dependencies.
 * Contains HTTP clients, API services, and repository implementations.
 */
val dataModule = module {
    // HTTP Client (uncomment when API is ready)
    // single { createHttpClient() }

    // API Services (uncomment when API is ready)
    // singleOf(::DashboardApiServiceImpl) bind DashboardApiService::class

    // Repositories
    singleOf(::DashboardRepositoryImpl) bind DashboardRepository::class
}
