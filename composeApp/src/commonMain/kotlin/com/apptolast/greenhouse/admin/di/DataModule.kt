package com.apptolast.greenhouse.admin.di

import org.koin.dsl.module

/**
 * Koin module for data layer dependencies.
 * Contains HTTP clients, API services, and repository implementations.
 */
val dataModule = module {
    // HTTP Client
    // single { createHttpClient() }

    // API Services
    // singleOf(::ApiService)

    // Repositories
    // singleOf(::RepositoryImpl) bind Repository::class
}
