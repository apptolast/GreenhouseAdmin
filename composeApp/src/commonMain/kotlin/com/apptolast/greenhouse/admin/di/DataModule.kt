package com.apptolast.greenhouse.admin.di

import com.apptolast.greenhouse.admin.data.local.LocaleManager
import com.apptolast.greenhouse.admin.data.local.TokenStorage
import com.apptolast.greenhouse.admin.data.remote.api.AlertsApiService
import com.apptolast.greenhouse.admin.data.remote.api.AuthApiService
import com.apptolast.greenhouse.admin.data.remote.api.CatalogApiService
import com.apptolast.greenhouse.admin.data.remote.api.DevicesApiService
import com.apptolast.greenhouse.admin.data.remote.api.GreenhousesApiService
import com.apptolast.greenhouse.admin.data.remote.api.SectorsApiService
import com.apptolast.greenhouse.admin.data.remote.api.SettingsApiService
import com.apptolast.greenhouse.admin.data.remote.api.TenantsApiService
import com.apptolast.greenhouse.admin.data.remote.api.UsersApiService
import com.apptolast.greenhouse.admin.data.remote.createHttpClient
import com.apptolast.greenhouse.admin.data.repository.AlertsRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.AuthRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.CatalogRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.ClientsRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.DashboardRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.DevicesRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.GreenhousesRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.SectorsRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.SettingsRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.UsersRepositoryImpl
import com.apptolast.greenhouse.admin.domain.auth.AuthEventManager
import com.apptolast.greenhouse.admin.domain.repository.AlertsRepository
import com.apptolast.greenhouse.admin.domain.repository.AuthRepository
import com.apptolast.greenhouse.admin.domain.repository.CatalogRepository
import com.apptolast.greenhouse.admin.domain.repository.ClientsRepository
import com.apptolast.greenhouse.admin.domain.repository.DashboardRepository
import com.apptolast.greenhouse.admin.domain.repository.DevicesRepository
import com.apptolast.greenhouse.admin.domain.repository.GreenhousesRepository
import com.apptolast.greenhouse.admin.domain.repository.SectorsRepository
import com.apptolast.greenhouse.admin.domain.repository.SettingsRepository
import com.apptolast.greenhouse.admin.domain.repository.UsersRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for data layer dependencies.
 * Contains HTTP clients, API services, and repository implementations.
 */
val dataModule = module {
    // Token Storage (platform-specific)
    singleOf(::TokenStorage)

    // Locale Manager
    singleOf(::LocaleManager)

    // Auth Event Manager (for session expiration events)
    singleOf(::AuthEventManager)

    // HTTP Client (uses TokenStorage for auth and AuthEventManager for session events)
    single { createHttpClient(get(), get()) }

    // API Services
    singleOf(::AuthApiService)
    singleOf(::TenantsApiService)
    singleOf(::UsersApiService)
    singleOf(::GreenhousesApiService)
    singleOf(::SectorsApiService)
    singleOf(::DevicesApiService)
    singleOf(::AlertsApiService)
    singleOf(::SettingsApiService)
    singleOf(::CatalogApiService)

    // Repositories
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::DashboardRepositoryImpl) bind DashboardRepository::class
    singleOf(::ClientsRepositoryImpl) bind ClientsRepository::class
    singleOf(::UsersRepositoryImpl) bind UsersRepository::class
    singleOf(::GreenhousesRepositoryImpl) bind GreenhousesRepository::class
    singleOf(::SectorsRepositoryImpl) bind SectorsRepository::class
    singleOf(::DevicesRepositoryImpl) bind DevicesRepository::class
    singleOf(::AlertsRepositoryImpl) bind AlertsRepository::class
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class
    singleOf(::CatalogRepositoryImpl) bind CatalogRepository::class
}
